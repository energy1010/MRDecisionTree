package com.younger.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.younger.bayes.NaiveBayes;
import com.younger.core.SystemInfo;
import com.younger.data.Data;
import com.younger.data.DataSet;
import com.younger.decisionTree.AbstractDecisionTree;
import com.younger.decisionTree.EntropyDecisionTree;
import com.younger.tool.Tool;
import com.younger.ui.LogWindow;
import com.younger.ui.tool.Messages;
import com.younger.xml.DomXml;

/**
 * 
 * @author Administrator
 * 
 */
public class DecisionTreeTest extends JFrame implements Observer {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1287609889543055813L;

	private static final Logger log = LoggerFactory
			.getLogger(EntropyDecisionTree.class);

	/** the gui mainForm */
	private static DecisionTreeTest m_mainForm;

	private static DataSet m_dataSet = null;
	
	/** */
	private File m_dataFile = null;
	/** dt classifier */
	private AbstractDecisionTree m_decisionTree = null;

	private NaiveBayes m_naiveBayes = null;
	
	
	
	private DefaultTableModel m_defaultTableModel = null;
	
	private JTable m_table = new JTable();
	
	private JTextArea m_classifierOutputTextArea = new JTextArea();
	// private Canvas treeView = new TreeCanvas(this.dataModel);
	
//	private Canvas m_view_right = new Canvas();
	private JTextArea m_treeView = new JTextArea();
	
	private JPanel statusPanel = new JPanel();
	
	private JScrollPane splitpane_left_top = new JScrollPane(this.m_table, 22, 32);
	private JScrollPane splitpane_left_bottom = new JScrollPane(this.m_treeView, 22, 32);
	private JScrollPane splitpane_right=new JScrollPane(m_classifierOutputTextArea,22,32);
	
	private JSplitPane splitpane_left = new JSplitPane(0, true,splitpane_left_top,splitpane_left_bottom);
	
	private JSplitPane splitpane_main = new JSplitPane(1, true, splitpane_left,
			splitpane_right);
	/** menu bar */
	private JMenuBar menuBar = new JMenuBar();
	
	/** menu file */
	private JMenu menu_fileMenu = new JMenu(
			Messages.getInstance().getString("main_menu_file"));
	private JMenuItem item_openFileItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_item_openfile"));
	private JMenuItem item_rawDataItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_item_rawfile"));
	private JMenuItem item_saveAsItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_item_saveAsfile"));
	
	/** menu tree */
	private JMenu menu_TreeJMenu = new JMenu(
			Messages.getInstance().getString("main_menu_tree"));
	private JMenuItem item_buildTreeItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_item_buildtree"));
	private JMenuItem item_showTreeItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_item_showtree"));
	private JMenuItem item_writeTreeItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_item_writetree"));

	/** menu classify */
	private JMenu menu_ClassifyJMenu = new JMenu(Messages.getInstance().getString("main_menu_classify"));
	
	/** bayes menu  */
	private JMenu menu_BayesJMenu = new JMenu(Messages.getInstance().getString("menu_menu_bayes"));
	private JMenuItem item_bayesLoadDataSetItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_item_bayesLoadDataSet"));
	private JMenuItem item_bayesClassifyItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_item_bayesclassify"));
	private JMenuItem item_bayesPredictItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_item_bayespredict"));
	
	/** help menu */
	private JMenu menu_HelpJMenu = new JMenu(
			Messages.getInstance().getString("main_menu_help"));
	private JMenuItem item_homePageItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_item_homepage"));
	private JMenuItem item_systemInfoItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_item_systeminfo"));
	private JMenuItem item_aboutItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_item_about"));
	
	/** language menu */
	private JMenu menu_LanguageJMenu = new JMenu(
			Messages.getInstance().getString("main_menu_language"));
	private JMenuItem item_chineseItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_language_item_chinese"));
	private JMenuItem item_englishItem = new JMenuItem(
			Messages.getInstance().getString("main_menu_language_item_english"));
	
	/** program menu */
	private JMenu menu_ProgramJMenu = new JMenu(
			Messages.getInstance().getString("main_menu_program")
			);
	 private JMenuItem item_logItem = new JMenuItem(Messages.getInstance().getString("main_menu_item_log"));


	 
	private DecisionTreeTest() {
		initGUI();
		initLocalVariables();
	}

	
	
	private void initLocalVariables() {
		this.m_decisionTree =new EntropyDecisionTree();
	}



	public static DecisionTreeTest instance() {
		if (m_mainForm==null) {
			m_mainForm= new DecisionTreeTest();
		}
		return m_mainForm;
	}
	
	/**
	 * init the menu 
	 */
	private void initJmenu() {
		menuBar.add(menu_fileMenu);
		menuBar.add(menu_TreeJMenu);
		menuBar.add(menu_ProgramJMenu);
//		menuBar.add(new JSeparator(),JSeparator.NEXT);
		menuBar.add(menu_HelpJMenu);
		menuBar.add(menu_LanguageJMenu);
		menuBar.add(menu_ClassifyJMenu);
		menuBar.add(menu_BayesJMenu);
		
		menu_fileMenu.add(item_rawDataItem).addActionListener(
				new action_seeRawDataFile());
		menu_fileMenu.add(item_openFileItem).addActionListener(
				new action_loadDataForDTTree());
		menu_fileMenu.add(item_saveAsItem).addActionListener(
				new action_loadDataForDTTree());
		menu_TreeJMenu.add(item_buildTreeItem).addActionListener(
				new action_buildTree());
		menu_TreeJMenu.add(item_showTreeItem).addActionListener(
				new action_showTree());
		menu_TreeJMenu.add(item_writeTreeItem).addActionListener(
				new action_writeTree());

		menu_ProgramJMenu.add(item_logItem).addActionListener(new action_saveAs());
		
		menu_HelpJMenu.add(item_homePageItem).addActionListener(
				new action_homePage());
		menu_HelpJMenu.add(item_systemInfoItem).addActionListener(
				new action_systemInfo());
		menu_HelpJMenu.add(item_aboutItem)
				.addActionListener(new action_about());

		menu_LanguageJMenu.add(item_chineseItem).addActionListener(
				new action_setLanguage());
		menu_LanguageJMenu.add(item_englishItem).addActionListener(
				new action_setLanguage());
		
		menu_BayesJMenu.add(item_bayesLoadDataSetItem).addActionListener(
				new action_loadDataSetForBayesClassifier());
		menu_BayesJMenu.add(item_bayesClassifyItem).addActionListener(
				new action_buildBayes());
		menu_BayesJMenu.add(item_bayesPredictItem).addActionListener(new action_predictDataSetForBayesClassfier());
//		menu_BayesJMenu.add(item_bayesClassifyItem).addActionListener(
//				new action_());
		// menu_LanguageJMenu.add(item_aboutItem).addActionListener(null);
		setJMenuBar(menuBar);

	}
	/**
	 * init Components
	 */
	private void initComponents(){
		this.statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		statusPanel.add(new JLabel("Decision Tree"));
		statusPanel.add(new JLabel( (new Date()).toString()) );
		m_treeView.setText(Messages.getInstance().getString("splitPanel_treeview") );
	}
	
	private void initGUI() {
		
		setTitle("Decision　Tree");
		initComponents();
		initPane();
		initJmenu();
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		// addWindowListener(new WindowAdapter() {
		// public void windowClosing(WindowEvent paramAnonymousWindowEvent) {
		// System.exit(0);
		// }
		// });
		
		  // setup splash screen
//	      mainForm.add(new  EventListener() {
//	    	   public void startUpComplete() {
//	 	          SplashWindow.disposeSplash();
//	 	        }
//	      }
//	      );
//	      SplashWindow.splash(ClassLoader.getSystemResource("weka/gui/images/weka_splash.gif"));
//		 SplashWindow.splash(
		setBounds(0, 0, 1024, 768);
		this.setVisible(true);
//		this.setExtendedState(MAXIMIZED_BOTH);
		this.setResizable(false);
	}

	/**
	 * init the main pane
	 */
	private void initPane() {
		Container localContainer = getContentPane();
		localContainer.setLayout(new BorderLayout());
		
		splitpane_left_top.setBorder(BorderFactory.createTitledBorder("Training Data"));
		splitpane_left_bottom.setBorder(BorderFactory.createTitledBorder("Tree View"));
		splitpane_left.setDividerLocation(400);
		splitpane_left.setLastDividerLocation(0);
		splitpane_left.setOneTouchExpandable(true);
		splitpane_right.setBorder(BorderFactory.createTitledBorder("Classifer Output"));
		splitpane_main.setDividerLocation(500);
		splitpane_main.setLastDividerLocation(0);
		splitpane_main.setOneTouchExpandable(true);
		localContainer.add(splitpane_main, "Center");
		localContainer.add(statusPanel,BorderLayout.SOUTH);
	}
	
	
	
	/**
	 * init the table model with instances
	 * @author apple
	 */
	private void loadData(DataSet dataSet){
		this.m_defaultTableModel = new DefaultTableModel();
//		m_table.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
//			@Override
//			public Component getTableCellRendererComponent(JTable table, Object value,
//					boolean isSelected, boolean hasFocus, int row, int column) {
//			if(row%2==0){
//				setBackground(new Color(206,231,255));//set light blue
//			}else{
//				setBackground(new Color(255,255,255));
//			}
//			return table;
////			return getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//			}
//			});
		this.m_defaultTableModel.addColumn("recordId");
		for (int j = 0; j < dataSet.getAttributeNum(); j++) {
			String attributeColumnName = dataSet
					.getAttributeNamesList().get(j);
			this.m_defaultTableModel.addColumn(attributeColumnName);
		}
		this.m_defaultTableModel.addColumn(dataSet.getClassName());
		for(Data data: dataSet.getDataList()){
			// row data vector
			Vector<String> vector=new Vector<String>();
			vector.add(data.getRecordId()+"");
			vector.addAll(data.getAttributevaluesList());
			vector.add(data.getClassValue() );
			this.m_defaultTableModel.addRow(vector);
		}
		
		this.m_table.setModel(this.m_defaultTableModel);
		m_table.createDefaultColumnsFromModel();
		this.m_table.setSelectionBackground(Color.cyan);
	}

	/**
	 * @deprecated
	 * with bug inside , node : the row num is not right
	 * @author apple
	 */
	private void loadData() {
		Vector<String> classValueStrings = new Vector<String>(this.m_decisionTree
				.getTraingData().getDatasetSize());
		this.m_defaultTableModel = new DefaultTableModel();
		for (int j = 0; j < this.m_decisionTree.getAttributeNum(); j++) {
			String attributeColumnName = this.m_decisionTree
					.getAttributeNamesList().get(j);
			this.m_defaultTableModel.addColumn(attributeColumnName);
		}
		try {
			// record Id start from 1;
			for (int i = 1; i <= this.m_decisionTree.getTraingData()
					.getDataList().size(); i++) {
				Object[] aObjects = m_decisionTree.getTraingData()
						.getDataList().get(i - 1)
						.getAttributevaluesList().toArray();
				String classValue = m_decisionTree.getTraingData()
						.getDataList().get(i - 1).getClassValue();
				classValueStrings.add(classValue);
				m_defaultTableModel.addRow(aObjects);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.m_defaultTableModel.addColumn("recordId", this.m_decisionTree
				.getTraingData().getRecordIds().toArray());
		this.m_defaultTableModel.addColumn("class", classValueStrings);
		this.m_table.setModel(this.m_defaultTableModel);
		m_table.createDefaultColumnsFromModel();
		this.m_table.moveColumn(this.m_defaultTableModel.findColumn("recordId"), 0);
		this.m_table.setSelectionBackground(Color.cyan);
		// this.table.setEnabled(false);
	}

	private File getFileFromFileChooser(){
		  JFileChooser localJFileChooser = new JFileChooser(new File("./file"));
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "ARFF & Data files", "arff", "data","txt");
		    localJFileChooser.setFileFilter(filter);//set the file filter
		//set the default open dir
//		localJFileChooser.setCurrentDirectory(new File("./file"));
		localJFileChooser.setAutoscrolls(true);
	//	localJFileChooser.addChoosableFileFilter(new ExtensionFileFilter(
		//		".data", Messages.getInstance().getString(
			//			"Main_InitGUI_ExtensionFileFilter_Text_Three")));
		//localJFileChooser.addChoosableFileFilter(new ExtensionFileFilter(
			//	".txt", Messages.getInstance().getString(
				//		"Main_InitGUI_ExtensionFileFilter_Text_Second")));
//		localJFileChooser.addChoosableFileFilter(new ExtensionFileFilter(
	//			".arff", Messages.getInstance().getString(
		//				"Main_InitGUI_ExtensionFileFilter_Text_First")));
		localJFileChooser.setMultiSelectionEnabled(false);
		localJFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//		localJFileChooser.showOpenDialog(DecisionTreeTest.this);
	    int returnVal = localJFileChooser.showOpenDialog(m_mainForm);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	     log.info("You chose to open this file: " +
	    		   localJFileChooser.getSelectedFile().getName());
	    }
	    return localJFileChooser.getSelectedFile();
	}
	
	
	/**
	 * load dataSet from file (.arff, .txt, .data, .dat .....)
	 * @author apple
	 *
	 */
	class action_loadDataForDTTree implements ActionListener {
		public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
			//init the file variable
			DecisionTreeTest.this.m_dataFile = getFileFromFileChooser();
			DecisionTreeTest.this.m_decisionTree.setRootNode(null);// rebuild tree
			//get the dataset from file
			if(DecisionTreeTest.this.m_decisionTree == null){
				DecisionTreeTest.this.m_decisionTree = new EntropyDecisionTree();
			}
			DecisionTreeTest.this.m_decisionTree.clearClassifier();
			DecisionTreeTest.m_dataSet= DecisionTreeTest.this.m_decisionTree.loadDataSet(m_dataFile.getAbsolutePath());
			DecisionTreeTest.this.loadData(DecisionTreeTest.m_dataSet);
			m_treeView.setText(Messages.getInstance().getString("splitPanel_treeview") );
			m_classifierOutputTextArea.setText("");
			StringBuffer sBuffer=new StringBuffer("\n====== load Data =======\n");
			sBuffer.append(DecisionTreeTest.m_dataSet.toString());
			sBuffer.append("\n");
			m_classifierOutputTextArea.append(sBuffer.toString());
			
		}
	}

	class action_seeRawDataFile implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(DecisionTreeTest.this.m_dataFile==null||!DecisionTreeTest.this.m_dataFile.isFile()){
//				JOptionPane  opt=new JOptionPane("please choose a file !", JOptionPane.ERROR_MESSAGE,JOptionPane.DEFAULT_OPTION);
//				opt.show(true);
				JOptionPane.showMessageDialog(m_mainForm, "please choose a file !", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			final JFrame rawDataFrame = new JFrame("Raw Data File");
			rawDataFrame.setResizable(false);
			rawDataFrame.setBounds(0, 0, 500, 500);
			List<String> lineStrings = null;
			if(m_dataFile!=null){
		      lineStrings = Tool
					.readFileByLines(DecisionTreeTest.this.m_dataFile.getAbsolutePath());
			}
			JTextArea textArea = new JTextArea();
			for (String string : lineStrings) {
				textArea.append(string + "\r\n");
			}
			rawDataFrame.setLayout(new BorderLayout());// set the layout
			final JScrollPane splitPane = new JScrollPane(textArea);
			rawDataFrame.getContentPane().add(splitPane, "Center");
			rawDataFrame.addWindowListener(new WindowAdapter() {
				public void windowClosed(WindowEvent e) {
					rawDataFrame.setVisible(false);
					try {
						this.finalize();
					} catch (Throwable e1) {
						e1.printStackTrace();
					}
				};
			});
			// rawDataFrame.p
			rawDataFrame.setLocation(10, 10);
			rawDataFrame.setVisible(true);
		}
	}

	/**
	 * Shows or hides this component depending on the value of parameter b.
	 * 
	 * @param b
	 *            if true, shows this component; otherwise, hides this component
	 */
	public void setVisible(boolean b) {
		super.setVisible(b);

		if (b)
			paint(this.getGraphics());
	}

	class action_buildTree implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				DecisionTreeTest.this.m_decisionTree.buildClassifier(DecisionTreeTest.this.m_decisionTree.getTraingData());
				StringBuffer sBuffer=new StringBuffer("\n====== build classifier =======\n");
				sBuffer.append(DecisionTreeTest.this.m_decisionTree.toString());
				sBuffer.append("\n");
				m_classifierOutputTextArea.append(sBuffer.toString());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	class action_showTree implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (DecisionTreeTest.this.m_decisionTree.getRootNode() != null) {
				DecisionTreeTest.this.m_decisionTree.showClassifier();
			} else {
					DecisionTreeTest.this.m_decisionTree .clearClassifier();
					DataSet trainDataSet = DecisionTreeTest.this.m_decisionTree.loadDataSet(DecisionTreeTest.this.m_dataFile.getAbsolutePath());
					DecisionTreeTest.this.m_decisionTree.buildClassifier(trainDataSet);
			}
			DecisionTreeTest.this.m_treeView.setText("");
			DecisionTreeTest.this.m_treeView.setText(DecisionTreeTest.this.m_decisionTree.showClassifier());
			DecisionTreeTest.this.splitpane_left_bottom.revalidate();
//			DecisionTreeTest.this.validate();
		}
	}
	/**
	 * write tree to xml file
	 * @author apple
	 *
	 */
	class action_writeTree implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			log.debug("write tree to xml ");
			System.out.println("write tree to xml ");
			if (DecisionTreeTest.this.m_decisionTree.getRootNode() == null) {
				try {
//					DecisionTreeTest.this.m_decisionTree.buildClassifier(DecisionTreeTest.this.m_decisionTree.getTraingData());
					new action_showTree().actionPerformed(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			JFileChooser fileChooser = new JFileChooser("./save");
			int option = fileChooser.showSaveDialog(DecisionTreeTest.this);
			if (option == JFileChooser.APPROVE_OPTION) {
				String fileName = fileChooser.getSelectedFile()
						.getAbsolutePath();
				DomXml.writeTreeToXml1(fileName,
						DecisionTreeTest.this.m_decisionTree);
			}
		}

	}

	class action_about implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			log.debug("action_about");
		}

	}

	
	class action_buildBayes implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(m_naiveBayes!=null){
			m_naiveBayes.clearClassifier();}
			DecisionTreeTest.this.m_naiveBayes.buildClassifier(DecisionTreeTest.m_dataSet);
			log.info( DecisionTreeTest.this.m_naiveBayes.getClassifyAccuracy(DecisionTreeTest.this.m_naiveBayes, DecisionTreeTest.m_dataSet)+""  );
			StringBuffer sBuffer=new StringBuffer("\n====== build classifier =======\n");
			sBuffer.append(DecisionTreeTest.this.m_naiveBayes
					.toString());
			sBuffer.append("\n");
			m_classifierOutputTextArea.append(sBuffer.toString());
		}
	}
	//
	// /**
	// * Kills the JVM if all windows have been closed.
	// */
	// private void checkExit() {
	//
	// if (!isVisible()
	// // applications
	// && (m_ExplorerFrame == null)
	// && (m_ExperimenterFrame == null)
	// && (m_KnowledgeFlowFrame == null)
	// && (m_SimpleCLI == null)
	// // tools
	// && (m_ArffViewers.size() == 0)
	// && (m_SqlViewerFrame == null)
	// && (m_EnsembleLibraryFrame == null)
	// // visualization
	// && (m_Plots.size() == 0)
	// && (m_ROCs.size() == 0)
	// && (m_TreeVisualizers.size() == 0)
	// && (m_GraphVisualizers.size() == 0)
	// && (m_BoundaryVisualizerFrame == null)
	// // help
	// && (m_SystemInfoFrame == null) ) {
	// System.exit(0);
	// }
	// }

	class action_loadDataSetForBayesClassifier implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			DecisionTreeTest.this.m_naiveBayes =   new NaiveBayes();
			DecisionTreeTest.this.m_dataFile = getFileFromFileChooser();
			DecisionTreeTest.this.m_dataSet = DecisionTreeTest.this.m_naiveBayes.loadDataSet(DecisionTreeTest.this.m_dataFile.getAbsolutePath());
			DecisionTreeTest.this.loadData(DecisionTreeTest.m_dataSet);
			m_classifierOutputTextArea.setText("");
			StringBuffer sBuffer=new StringBuffer("\n====== load Data =======\n");
			sBuffer.append(DecisionTreeTest.m_dataSet.toString());
			sBuffer.append("\n");
			m_classifierOutputTextArea.append(sBuffer.toString());
		}
	}
	
	
	
	class action_predictDataSetForBayesClassfier implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(m_naiveBayes==null){
				new action_buildBayes().actionPerformed(e);
			}
			File preFile = 	getFileFromFileChooser();
//			DataSet dataSet = m_naiveBayes.loadDataSet(preFile.getAbsolutePath());
//			JFrame popFrame = new JFrame("Predict ");
//			popFrame.setLayout(new BorderLayout());
//			JTextArea jTextArea = new JTextArea();
//			popFrame.getContentPane().add(jTextArea);
//			popFrame.setVisible(true);
			
			List<Integer> list = new ArrayList<Integer>();
		List<String> listString = Tool.readFileByLines(preFile.getAbsolutePath()); //get the
		List<String> arrt=new ArrayList<String>();
		 for (int f = 0; f < listString.size(); f++) {
			 String lineString =Tool.formatStr(listString.get(f), " +");
			 log.info(lineString);
		 if(lineString!=""){
			 String re=Tool.removeReduntSpace(lineString);
			 if(re.equals("")||re.isEmpty()) {
				 continue;
			 }
			 log.info("re"+re);
			 arrt.clear();
			 String[] strings=lineString.split(",");
		for(String s:strings) {
			String attrval = Tool.removeReduntSpace(s);
			arrt .add(attrval);
		}
		 log.info("arr"+arrt.toString());
			int classIndex = m_naiveBayes.classifyInstanceReturnClassValueIndex(m_naiveBayes,arrt);
			log.info(classIndex+"");
			list.add(classIndex);
		 	}//if
		 }
		 StringBuffer sBuffer=new StringBuffer("\n====== predict Data =======\n");
			sBuffer.append(list.toString());
			sBuffer.append("\n");
			m_classifierOutputTextArea.append(sBuffer.toString());
		}
		
	}
	
	class action_saveAs implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (DecisionTreeTest.this.m_dataFile != null) {
				try {
					JFileChooser saveChooser = new JFileChooser("./save");
					saveChooser.setDialogType(JFileChooser.SAVE_DIALOG);//设置保存对话框
					//将设置好了的两种文件过滤器添加到文件选择器中来
					int index = saveChooser.showDialog(m_mainForm,Messages.getInstance().getString("Dialog_saveas"));
					if (index == JFileChooser.APPROVE_OPTION) {
						File f = saveChooser.getSelectedFile();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			JFileChooser fileChooser = new JFileChooser("./save");
			int option = fileChooser.showSaveDialog(DecisionTreeTest.this);
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if(file.isDirectory()||(!m_dataFile.exists())){return ;}
				if(file.exists()){
					log.info("dest　file　exist,delete it!");
					if(!file.delete()){
						log.error("delete failed");
						return;
					}
				}
				if(!file.getParentFile().exists()){
					log.info("the dest file fold doesn't exist");
					if(!file.getParentFile().mkdirs()){
						log.error("create dir failed!");
						return;
					}
				}
				int byteread = 0;
				InputStream inputStream = null;
				OutputStream outputStream= null;
				try {
					byte[] buffer=new byte[1024];
					inputStream = 	new FileInputStream(m_dataFile);
					outputStream= new FileOutputStream(file);
					while ( (byteread=inputStream.read(buffer))!=-1) {
						outputStream.write(buffer,0,byteread);
					}
					log.debug(String.format("copy file from %s to %s succefully!",m_dataFile,file) );
				} catch (Exception e2) {
					e2.printStackTrace();
					log.error(e2.getMessage());
				}finally{
					if(outputStream!=null){
						try {
							outputStream.close();
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
					if(inputStream!=null){
						try {
							inputStream.close();
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}
				
		}
			}
		}
	}
	
	class action_setLanguage implements ActionListener {

		private Locale[] locales = {Locale.CHINA, Locale.US };
		private Locale currentLocale;

		@Override
		public void actionPerformed(ActionEvent e) {
			// System.out.println(e.getActionCommand());
			Locale newLocale = null;
			if (e.getActionCommand().equalsIgnoreCase("中文")
					|| e.getActionCommand().equalsIgnoreCase("chinese")) {
				newLocale = Locale.CHINA;
			} else {
				newLocale = Locale.ENGLISH;
			}
			setCurrentLocale(newLocale);
			validate();
		}

		public void setCurrentLocale(Locale newlocale) {
			currentLocale = newlocale;
			updateDisplay();
		}

		public void updateDisplay() {
			// log.debug(Messages.getString(currentLocale,"main_menu_language_item_chinese"));
			// log.debug(Messages.getString(currentLocale,"main_menu_language_item_buildtree"));
			DecisionTreeTest.this.menu_fileMenu.setText(Messages.getInstance().getString(
					currentLocale, "main_menu_file"));
			DecisionTreeTest.this.menu_HelpJMenu.setText(Messages.getInstance().getString(
					currentLocale, "main_menu_help"));
			DecisionTreeTest.this.menu_LanguageJMenu.setText(Messages
					.getInstance().getString(currentLocale, "main_menu_language"));
			DecisionTreeTest.this.menu_TreeJMenu.setText(Messages.getInstance().getString(
					currentLocale, "main_menu_tree"));
			DecisionTreeTest.this.menu_ProgramJMenu.setText(Messages.getInstance().getString(
					currentLocale, "main_menu_program"));
			
			DecisionTreeTest.this.item_chineseItem.setText(Messages.getInstance().getString(
					currentLocale, "main_menu_language_item_chinese"));
			DecisionTreeTest.this.item_englishItem.setText(Messages.getInstance().getString(
					currentLocale, "main_menu_language_item_english"));
			DecisionTreeTest.this.item_aboutItem.setText(Messages.getInstance().getString(
					currentLocale, "main_menu_item_about"));
			DecisionTreeTest.this.item_buildTreeItem.setText(Messages
					.getInstance().getString(currentLocale, "main_menu_item_buildtree"));
			DecisionTreeTest.this.item_homePageItem.setText(Messages.getInstance().getString(
					currentLocale, "main_menu_item_homepage"));
			DecisionTreeTest.this.item_openFileItem.setText(Messages.getInstance().getString(
					currentLocale, "main_menu_item_openfile"));
			DecisionTreeTest.this.item_rawDataItem.setText(Messages.getInstance().getString(
					currentLocale, "main_menu_item_rawfile"));
			DecisionTreeTest.this.item_showTreeItem.setText(Messages.getInstance().getString(
					currentLocale, "main_menu_item_showtree"));
			DecisionTreeTest.this.item_systemInfoItem.setText(Messages
					.getInstance().getString(currentLocale, "main_menu_item_systeminfo"));
			DecisionTreeTest.this.item_writeTreeItem.setText(Messages
					.getInstance().getString(currentLocale, "main_menu_item_writetree"));
			DecisionTreeTest.this.item_logItem.setText(Messages
					.getInstance().getString(currentLocale, "main_menu_item_log"));
			// DecisionTreeTest.this.item_englishItem.setText(Messages.getString(currentLocale,"main_menu_language_item_english"));
			// DecisionTreeTest.this.item_englishItem.setText(Messages.getString(currentLocale,"main_menu_language_item_english"));
		}

	}

	class action_systemInfo implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// final JFrame m_SystemInfoFrame = null;
			// if (m_SystemInfoFrame == null) {
			item_systemInfoItem.setEnabled(false);
			final JFrame m_SystemInfoFrame = new JFrame(Messages.getInstance()
					.getString("GUIChooser_SystemInfo_JFrame_Text"));
			// m_SystemInfoFrame.setIconImage(m_Icon);
			m_SystemInfoFrame.getContentPane().setLayout(new BorderLayout());
			// get info
			Hashtable info = new SystemInfo().getSystemInfo();
			// sort names
			Vector names = new Vector();
			Enumeration enm = info.keys();
			while (enm.hasMoreElements())
				names.add(enm.nextElement());
			Collections.sort(names);
			// generate table
			String[][] data = new String[info.size()][2];
			for (int i = 0; i < names.size(); i++) {
				data[i][0] = names.get(i).toString();
				data[i][1] = info.get(data[i][0]).toString();
			}
			String[] titles = new String[] { "key", "value" };
			// String[] titles = new
			// String[]{Messages.getInstance().getString("GUIChooser_SystemInfo_TitleKey_Text"),
			// Messages.getInstance().getString("GUIChooser_SystemInfo_TitleValue_Text")};
			JTable table = new JTable(data, titles);
			m_SystemInfoFrame.getContentPane().add(new JScrollPane(table),
					BorderLayout.CENTER);
			m_SystemInfoFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent w) {
					m_SystemInfoFrame.dispose();
					// m_SystemInfoFrame = null;
					item_systemInfoItem.setEnabled(true);
					// checkExit();
					m_SystemInfoFrame.setVisible(false);
				}
			});
			m_SystemInfoFrame.pack();
			m_SystemInfoFrame.setSize(800, 600);
			m_SystemInfoFrame.setVisible(true);
		}
	}

	class action_log implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			 LogWindow m_LogWindow = new LogWindow();
			 m_LogWindow.setVisible(true);
		}
		
	}
	
	class action_homePage implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			log.debug("home page");
		}

	}

	@Override
	public void update(Observable o, Object arg) {
		
	}
}