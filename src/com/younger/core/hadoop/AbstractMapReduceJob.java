package com.younger.core.hadoop;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.mahout.common.AbstractJob;
import org.apache.mahout.common.commandline.DefaultOptionCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * <p>
 * Superclass of many Mahout Hadoop "jobs". A job drives configuration and
 * launch of one or more maps and reduces in order to accomplish some task.
 * </p>
 * 
 * <p>
 * Command line arguments available to all subclasses are:
 * </p>
 * 
 * <ul>
 * <li>--tempDir (path): Specifies a directory where the job may place temp
 * files (default "temp")</li>
 * <li>--help: Show help message</li>
 * </ul>
 * 
 * <p>
 * In addition, note some key command line parameters that are parsed by Hadoop,
 * which jobs may need to set:
 * </p>
 * 
 * <ul>
 * <li>-Dmapred.job.name=(name): Sets the Hadoop task names. It will be suffixed
 * by the mapper and reducer class names</li>
 * <li>-Dmapred.output.compress={true,false}: Compress final output (default
 * true)</li>
 * <li>-Dmapred.input.dir=(path): input file, or directory containing input
 * files (required)</li>
 * <li>-Dmapred.output.dir=(path): path to write output files (required)</li>
 * </ul>
 * 
 * <p>
 * Note that because of how Hadoop parses arguments, all "-D" arguments must
 * appear before all other arguments.
 * </p>
 */
public abstract class AbstractMapReduceJob extends AbstractJob {

	private static final Logger log = LoggerFactory
			.getLogger(AbstractMapReduceJob.class);

	/** option used to specify the input path */
	private Option inputOption;

	/** option used to specify the output path */
	private Option outputOption;

	private String uriPrefixString ;
	
	/** input path, populated by {@link #parseArguments(String[])} */
	private Path inputPath;

	/** output path, populated by {@link #parseArguments(String[])  */
	private Path outputPath;

	private Map<String, String> argMap;

	/** internal list of options that have been added */
	private final List<Option> options;

	protected AbstractMapReduceJob() {
		options = new LinkedList<Option>();
	}

	/**
	 * Returns the input path established by a call to
	 * {@link #parseArguments(String[])}. The source of the path may be an input
	 * option added using {@link #addInputOption()} or it may be the value of
	 * the {@code mapred.input.dir} configuration property.
	 */
	public Path getInputPath() {
		return inputPath;
	}

	/**
	 * Returns the output path established by a call to
	 * {@link #parseArguments(String[])}. The source of the path may be an
	 * output option added using {@link #addOutputOption()} or it may be the
	 * value of the {@code mapred.input.dir} configuration property.
	 */
	public Path getOutputPath() {
		return outputPath;
	}

	/**
	 * Add an option with no argument whose presence can be checked for using
	 * {@code containsKey} method on the map returned by
	 * {@link #parseArguments(String[])};
	 */
	protected void addFlag(String name, String shortName, String description) {
		options.add(buildOption(name, shortName, description, false, false,
				null));
	}

	/**
	 * Add an option to the the set of options this job will parse when
	 * {@link #parseArguments(String[])} is called. This options has an argument
	 * with null as its default value.
	 */
	protected void addOption(String name, String shortName, String description) {
		options.add(buildOption(name, shortName, description, true, false, null));
	}

	/**
	 * Add an option to the the set of options this job will parse when
	 * {@link #parseArguments(String[])} is called.
	 * 
	 * @param required
	 *            if true the {@link #parseArguments(String[])} will throw fail
	 *            with an error and usage message if this option is not
	 *            specified on the command line.
	 */
	protected void addOption(String name, String shortName, String description,
			boolean required) {
		options.add(buildOption(name, shortName, description, true, required,
				null));
	}

	/**
	 * Add an option to the the set of options this job will parse when
	 * {@link #parseArguments(String[])} is called. If this option is not
	 * specified on the command line the default value will be used.
	 * 
	 * @param defaultValue
	 *            the default argument value if this argument is not found on
	 *            the command-line. null is allowed.
	 */
	protected void addOption(String name, String shortName, String description,
			String defaultValue) {
		options.add(buildOption(name, shortName, description, true, false,
				defaultValue));
	}

	/**
	 * Add an arbitrary option to the set of options this job will parse when
	 * {@link #parseArguments(String[])} is called. If this option has no
	 * argument, use {@code containsKey} on the map returned by
	 * {@code parseArguments} to check for its presence. Otherwise, the string
	 * value of the option will be placed in the map using a key equal to this
	 * options long name preceded by '--'.
	 * 
	 * @return the option added.
	 */
	protected Option addOption(Option option) {
		options.add(option);
		return option;
	}

	/**
	 * Add the default input directory option, '-i' which takes a directory name
	 * as an argument. When {@link #parseArguments(String[])} is called, the
	 * inputPath will be set based upon the value for this option. If this
	 * method is called, the input is required.
	 */
	protected void addInputOption() {
		this.inputOption = addOption(DefaultOptionCreator.inputOption()
				.create());
	}

	/**
	 * Add the default output directory option, '-o' which takes a directory
	 * name as an argument. When {@link #parseArguments(String[])} is called,
	 * the outputPath will be set based upon the value for this option. If this
	 * method is called, the output is required.
	 */
	protected void addOutputOption() {
		this.outputOption = addOption(DefaultOptionCreator.outputOption()
				.create());
	}

	/**
	 * Build an option with the given parameters. Name and description are
	 * required.
	 * 
	 * @param name
	 *            the long name of the option prefixed with '--' on the
	 *            command-line
	 * @param shortName
	 *            the short name of the option, prefixed with '-' on the
	 *            command-line
	 * @param description
	 *            description of the option displayed in help method
	 * @param hasArg
	 *            true if the option has an argument.
	 * @param required
	 *            true if the option is required.
	 * @param defaultValue
	 *            default argument value, can be null.
	 * @return the option.
	 */
	protected static Option buildOption(String name, String shortName,
			String description, boolean hasArg, boolean required,
			String defaultValue) {

		DefaultOptionBuilder optBuilder = new DefaultOptionBuilder()
				.withLongName(name).withDescription(description)
				.withRequired(required);

		if (shortName != null) {
			optBuilder.withShortName(shortName);
		}

		if (hasArg) {
			ArgumentBuilder argBuilder = new ArgumentBuilder().withName(name)
					.withMinimum(1).withMaximum(1);

			if (defaultValue != null) {
				argBuilder = argBuilder.withDefault(defaultValue);
			}

			optBuilder.withArgument(argBuilder.create());
		}

		return optBuilder.create();
	}

	/**
	 * Build the option key (--name) from the option name
	 */
	public static String keyFor(String optionName) {
		return "--" + optionName;
	}

	/**
	 * @return the requested option, or null if it has not been specified
	 */
	public String getOption(String optionName) {
		return argMap.get(keyFor(optionName));
	}

	/**
	 * @return if the requested option has been specified
	 */
	public boolean hasOption(String optionName) {
		return argMap.containsKey(keyFor(optionName));
	}

	/**
	 * Obtain input and output directories from command-line options or hadoop
	 * properties. If {@code addInputOption} or {@code addOutputOption} has been
	 * called, this method will throw an {@code OptionException} if no source
	 * (command-line or property) for that value is present. Otherwise,
	 * {@code inputPath} or {@code outputPath} will be non-null only if specified
	 * as a hadoop property. Command-line options take precedence over hadoop
	 * properties.
	 * 
	 * @param cmdLine
	 * @throws IllegalArgumentException
	 *             if either inputOption is present, and neither {@code --input}
	 *             nor {@code -Dmapred.input dir} are specified or outputOption
	 *             is present and neither {@code --output} nor
	 *             {@code -Dmapred.output.dir} are specified.
	 */
	protected void parseDirectories(CommandLine cmdLine) {

		Configuration conf = getConf();

		if (inputOption != null && cmdLine.hasOption(inputOption)) {
			this.inputPath = new Path(cmdLine.getValue(inputOption).toString());
		}
		if (inputPath == null && conf.get("mapred.input.dir") != null) {
			this.inputPath = new Path(conf.get("mapred.input.dir"));
		}

		if (outputOption != null && cmdLine.hasOption(outputOption)) {
			this.outputPath = new Path(cmdLine.getValue(outputOption)
					.toString());
		}
		if (outputPath == null && conf.get("mapred.output.dir") != null) {
			this.outputPath = new Path(conf.get("mapred.output.dir"));
		}

		Preconditions
				.checkArgument(
						inputOption == null || inputPath != null,
						"No input specified or -Dmapred.input.dir must be provided to specify input directory");
		Preconditions
				.checkArgument(
						outputOption == null || outputPath != null,
						"No output specified:  or -Dmapred.output.dir must be provided to specify output directory");
	}

	protected static void maybePut(Map<String, String> args,
			CommandLine cmdLine, Option... opt) {
		for (Option o : opt) {

			// the option appeared on the command-line, or it has a value
			// (which is likely a default value).
			if (cmdLine.hasOption(o) || cmdLine.getValue(o) != null) {

				// nulls are ok, for cases where options are simple flags.
				Object vo = cmdLine.getValue(o);
				String value = vo == null ? null : vo.toString();
				args.put(o.getPreferredName(), value);
			}
		}
	}

	protected static boolean shouldRunNextPhase(Map<String, String> args,
			AtomicInteger currentPhase) {
		int phase = currentPhase.getAndIncrement();
		String startPhase = args.get("--startPhase");
		String endPhase = args.get("--endPhase");
		boolean phaseSkipped = (startPhase != null && phase < Integer
				.parseInt(startPhase))
				|| (endPhase != null && phase > Integer.parseInt(endPhase));
		if (phaseSkipped) {
			log.info("Skipping phase {}", phase);
		}
		return !phaseSkipped;
	}

	public String getUriPrefixString() {
		return this.uriPrefixString;
	}

	public void setUriPrefixString(String uriPrefixString) {
		this.uriPrefixString = uriPrefixString;
	}

	/**
	 * @deprecated
	 */
	public Job prepareJob( Path inputPath, Path outputPath,
			Class<? extends InputFormat> inputFormat,
			Class<? extends Mapper> mapperClass,
			Class<? extends Writable> mapperOutKeyClass,
			Class<? extends Writable> mapperOutValClass,
			Class<? extends Reducer> reducerClass,
			Class<? extends Writable> reducerOutPutKeyClass,
			Class<? extends Writable> reducerOutPutValueClass,
			Class<? extends OutputFormat> outputFormat) throws IOException {

		Job job = new Job(new Configuration(getConf()));
		Configuration jobConf = job.getConfiguration();

		if (reducerClass.equals(Reducer.class)) {
			if (mapperClass.equals(Mapper.class)) {
				throw new IllegalStateException(
						"Can't figure out the user class jar file from mapper/reducer");
			}
			job.setJarByClass(mapperClass);
		} else {
			job.setJarByClass(reducerClass);
		}

		job.setInputFormatClass(inputFormat);
		jobConf.set("mapred.input.dir", inputPath.toString());

		job.setMapperClass(mapperClass);
		job.setMapOutputKeyClass(mapperOutKeyClass);
		job.setMapOutputValueClass(mapperOutValClass);

		jobConf.setBoolean("mapred.compress.map.output", true);

		job.setReducerClass(reducerClass);
		job.setOutputKeyClass(reducerOutPutKeyClass);
		job.setOutputValueClass(reducerOutPutValueClass);

		job.setJobName(getCustomJobName(job, mapperClass, reducerClass));

		job.setOutputFormatClass(outputFormat);
		jobConf.set("mapred.output.dir", outputPath.toString());

		return job;
	}

	/**
	 * set the job's jobName 
	 * @deprecated
	 */
	public Job prepareJob(String jobName, Path inputPath, Path outputPath,
			Class<? extends InputFormat> inputFormat,
			Class<? extends Mapper> mapperClass,
			Class<? extends Writable> mapperOutKeyClass,
			Class<? extends Writable> mapperOutValClass,
			Class<? extends Reducer> reducerClass,
			Class<? extends Writable> reducerOutPutKeyClass,
			Class<? extends Writable> reducerOutPutValueClass,
			Class<? extends OutputFormat> outputFormat) throws IOException {
		
		Job job = new Job(new Configuration(getConf()) ,jobName);
		Configuration jobConf = job.getConfiguration();

		if (reducerClass.equals(Reducer.class)) {
			if (mapperClass.equals(Mapper.class)) {
				throw new IllegalStateException(
						"Can't figure out the user class jar file from mapper/reducer");
			}
			job.setJarByClass(mapperClass);
		} else {
			job.setJarByClass(reducerClass);
		}
		job.setJobName(jobName);
		job.setInputFormatClass(inputFormat);
		jobConf.set("mapred.input.dir", inputPath.toString());

		job.setMapperClass(mapperClass);
		job.setMapOutputKeyClass(mapperOutKeyClass);
		job.setMapOutputValueClass(mapperOutValClass);

		jobConf.setBoolean("mapred.compress.map.output", true);

		job.setReducerClass(reducerClass);
		job.setOutputKeyClass(reducerOutPutKeyClass);
		job.setOutputValueClass(reducerOutPutValueClass);

		job.setJobName(getCustomJobName(job, mapperClass, reducerClass));

		job.setOutputFormatClass(outputFormat);
		jobConf.set("mapred.output.dir", outputPath.toString());

		return job;
	}
	
	
	/**
	 * set the job's jarClass , conf, 
	 */
	public Job prepareJob(Class<?> jarClass,Configuration configuration,String jobName, Path inputPath, Path outputPath,
			Class<? extends InputFormat> inputFormat,
			Class<? extends Mapper> mapperClass,
			Class<? extends Writable> mapperOutKeyClass,
			Class<? extends Writable> mapperOutValClass,
			Class<? extends Reducer> reducerClass,
			Class<? extends Writable> reducerOutPutKeyClass,
			Class<? extends Writable> reducerOutPutValueClass,
			Class<? extends OutputFormat> outputFormat) throws IOException {
		
		Job job = new Job(configuration ,jobName);
		job.setJarByClass(jarClass);
		if (reducerClass.equals(Reducer.class)) {
			if (mapperClass.equals(Mapper.class)) {
				throw new IllegalStateException(
						"Can't figure out the user class jar file from mapper/reducer");
			}
			job.setJarByClass(mapperClass);
		} else {
			job.setJarByClass(reducerClass);
		}
		job.setJobName(jobName);
		job.setInputFormatClass(inputFormat);
		configuration.set("mapred.input.dir", inputPath.toString());

		job.setMapperClass(mapperClass);
		job.setMapOutputKeyClass(mapperOutKeyClass);
		job.setMapOutputValueClass(mapperOutValClass);

		configuration.setBoolean("mapred.compress.map.output", true);

		job.setReducerClass(reducerClass);
		job.setOutputKeyClass(reducerOutPutKeyClass);
		job.setOutputValueClass(reducerOutPutValueClass);

		job.setJobName(getCustomJobName(job, mapperClass, reducerClass));

		job.setOutputFormatClass(outputFormat);
		configuration.set("mapred.output.dir", outputPath.toString());

		return job;
	}
	
	/**
	 * set the job's jarClass , conf, 
	 * @param fsDefaultName eg "hdfs://localhost:9000"
	 * @param jarClass 
	 * @param Configuration
	 * @param jobName
	 * @param inputPath
	 * @param outputPath
	 */
	public Job prepareJob(String fsDefaultName,Class<?> jarClass,Configuration configuration,String jobName, Path inputPath, Path outputPath,
			Class<? extends InputFormat> inputFormat,
			Class<? extends Mapper> mapperClass,
			Class<? extends Writable> mapperOutKeyClass,
			Class<? extends Writable> mapperOutValClass,
			Class<? extends Reducer> reducerClass,
			Class<? extends Writable> reducerOutPutKeyClass,
			Class<? extends Writable> reducerOutPutValueClass,
			Class<? extends OutputFormat> outputFormat) throws IOException {
		configuration.set("fs.default.name", "hdfs://localhost:9000");
		Job job = new Job(configuration ,jobName);
		job.setJarByClass(jarClass);
		if (reducerClass.equals(Reducer.class)) {
			if (mapperClass.equals(Mapper.class)) {
				throw new IllegalStateException(
						"Can't figure out the user class jar file from mapper/reducer");
			}
			job.setJarByClass(mapperClass);
		} else {
			job.setJarByClass(reducerClass);
		}
		job.setJobName(jobName);
		job.setInputFormatClass(inputFormat);
		configuration.set("mapred.input.dir", inputPath.toString());
//		job.getConfiguration().set("fs.default.name", fsDefaultName);
		job.setMapperClass(mapperClass);
		job.setMapOutputKeyClass(mapperOutKeyClass);
		job.setMapOutputValueClass(mapperOutValClass);

		configuration.setBoolean("mapred.compress.map.output", true);

		job.setReducerClass(reducerClass);
		job.setOutputKeyClass(reducerOutPutKeyClass);
		job.setOutputValueClass(reducerOutPutValueClass);

		job.setJobName(getCustomJobName(job, mapperClass, reducerClass));

		job.setOutputFormatClass(outputFormat);
		configuration.set("mapred.output.dir", outputPath.toString());

		return job;
	}
	
	
	
	public Job prepareJob(String fsDefaultName,Class<?> jarClass,Configuration configuration,String jobName, String inputPathString, String outputPathString,
			Class<? extends InputFormat> inputFormat,
			Class<? extends Mapper> mapperClass,
			Class<? extends Writable> mapperOutKeyClass,
			Class<? extends Writable> mapperOutValClass,
			Class<? extends Reducer> reducerClass,
			Class<? extends Writable> reducerOutPutKeyClass,
			Class<? extends Writable> reducerOutPutValueClass,
			Class<? extends OutputFormat> outputFormat) throws IOException {
		configuration.set("fs.default.name", "hdfs://localhost:9000");
		Job job = new Job(configuration ,jobName);
		job.setJarByClass(jarClass);
		if (reducerClass.equals(Reducer.class)) {
			if (mapperClass.equals(Mapper.class)) {
				throw new IllegalStateException(
						"Can't figure out the user class jar file from mapper/reducer");
			}
			job.setJarByClass(mapperClass);
		} else {
			job.setJarByClass(reducerClass);
		}
		job.setJobName(jobName);
		job.setInputFormatClass(inputFormat);
		configuration.set("mapred.input.dir", inputPathString);
		job.setMapperClass(mapperClass);
		job.setMapOutputKeyClass(mapperOutKeyClass);
		job.setMapOutputValueClass(mapperOutValClass);

		configuration.setBoolean("mapred.compress.map.output", true);

		job.setReducerClass(reducerClass);
		job.setOutputKeyClass(reducerOutPutKeyClass);
		job.setOutputValueClass(reducerOutPutValueClass);

		job.setJobName(getCustomJobName(job, mapperClass, reducerClass));

		job.setOutputFormatClass(outputFormat);
		configuration.set("mapred.output.dir", outputPathString);

		return job;
	}
	
	public JobConf configureJobConf(String  fsDefaultName,Class<?> jarClass,String jobName, String inputPathString, String outputPathString,
			Class<? extends InputFormat> inputFormat,
			Class<? extends Mapper> mapperClass,
			Class<? extends Writable> mapperOutKeyClass,
			Class<? extends Writable> mapperOutValClass,
			Class<? extends Reducer> reducerClass,
			Class<? extends Writable> reducerOutPutKeyClass,
			Class<? extends Writable> reducerOutPutValueClass,
			Class<? extends OutputFormat> outputFormat){
		JobConf jobConf = new JobConf();
		jobConf.set("fs.default.name", "hdfs://localhost:9000");
		jobConf.setJarByClass(jarClass);
		jobConf.setJobName(jobName);
		jobConf.set("mapred.input.dir", inputPathString);
		jobConf.set("mapred.output.dir", outputPathString);
		jobConf.setInputFormat((Class<? extends org.apache.hadoop.mapred.InputFormat>) inputFormat);
		jobConf.setMapperClass((Class<? extends org.apache.hadoop.mapred.Mapper>) mapperClass);
		jobConf.setMapOutputKeyClass(mapperOutKeyClass);
		jobConf.setMapOutputValueClass(mapperOutValClass);
		jobConf.setReducerClass((Class<? extends org.apache.hadoop.mapred.Reducer>) reducerClass);
		jobConf.setOutputKeyClass(reducerOutPutKeyClass);
		jobConf.setOutputValueClass(reducerOutPutValueClass);
		return jobConf;
	}
	
	private String getCustomJobName(JobContext job,
			Class<? extends Mapper> mapper, Class<? extends Reducer> reducer) {
		StringBuilder name = new StringBuilder(100);
		String customJobName = job.getJobName();
		if (customJobName == null || customJobName.trim().length() == 0) {
			name.append(getClass().getSimpleName());
		} else {
			name.append(customJobName);
		}
		name.append('-').append(mapper.getSimpleName());
		name.append('-').append(reducer.getSimpleName());
		return name.toString();
	}

	public Option getInputOption() {
		return this.inputOption;
	}

	public void setInputOption(Option inputOption) {
		this.inputOption = inputOption;
	}

	public Option getOutputOption() {
		return this.outputOption;
	}

	public void setOutputOption(Option outputOption) {
		this.outputOption = outputOption;
	}

	public Map<String, String> getArgMap() {
		return this.argMap;
	}

	public void setArgMap(Map<String, String> argMap) {
		this.argMap = argMap;
	}

	public static Logger getLog() {
		return log;
	}

	public List<Option> getOptions() {
		return this.options;
	}

	public void setInputPath(Path inputPath) {
		this.inputPath = inputPath;
	}

	public void setOutputPath(Path outputPath) {
		this.outputPath = outputPath;
	}

//   public static JobConf createJobConf(Configuration conf) {
//		    JobConf jobconf = new JobConf(conf, DistCh.class);
//		    jobconf.setJobName(NAME);
//		    jobconf.setMapSpeculativeExecution(false);
//
//		    jobconf.setInputFormat(ChangeInputFormat.class);
//		    jobconf.setOutputKeyClass(Text.class);
//		    jobconf.setOutputValueClass(Text.class);
//
//		    jobconf.setMapperClass(ChangeFilesMapper.class);
//		    jobconf.setNumReduceTasks(0);
//		    return jobconf;
//		  }
	
}
