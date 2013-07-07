package com.younger.tool;

public class SortUtil {
	
	

	/**
     *冒泡排序 
     *@paramsrc待排序数组
     * */
	 public static   void doBubbleSort(int[] src)
    {
       int len=src.length;
       for(int i=0;i<len;i++)
       {
           for(int j=i+1;j<len;j++)
           {
              int temp;
              if(src[i]>src[j])
              {
                  temp=src[j];
                  src[j]=src[i];
                  src[i]=temp;
              }             
           }
//           printResult(i,src);
       }      
    }
    
    
    /**
     *选择排序
     *@paramsrc待排序的数组
     */
	 public static   void doChooseSort(int[] src)
    {
       int len=src.length;
       int temp;
       for(int i=0;i<len;i++)
       {
           temp=src[i];
           int j;
           int samllestLocation=i;//最小数的下标
           for(j=i+1;j<len;j++)
           {
              if(src[j]<temp)
              {
                  temp=src[j];//取出最小值
                  samllestLocation=j;//取出最小值所在下标
              }
           }
           src[samllestLocation]=src[i];
           src[i]=temp;
//           printResult(i,src);
       }
    }
    
    /**
     *插入排序(WHILE循环实现)
     *@paramsrc待排序数组
     */
 public static   void doInsertSort1(int[] src)
    {
       int len=src.length;
       for(int i=1;i<len;i++)
       {   
           int temp=src[i];
           int j=i;
           while(src[j-1]>temp)
           {
              src[j]=src[j-1];
              j--;
              if(j<=0)
                  break;
           }
           src[j]=temp;
//           printResult(i+1,src);
       }
    }
    
    
    
    public static void quickSort1(int[] pData,int left,int right)
    {
     int i ,j ;
     int middle,temp ;
     i = left ;
     j = right ;
     middle = pData[left] ;
     while(true)
     {
      while((++i)<right-1 && pData[i]<middle) ;
      while((--j)>left && pData[j]>middle) ;
      if(i>=j)
       break ;
         temp = pData[i] ;
         pData[i] = pData[j] ;
         pData[j] = temp ;
      
     }
     pData[left] = pData[j] ;
     pData[j] = middle ;
        
     if(left<j)
      quickSort1(pData,left,j) ;
     
     if(right>i)
      quickSort1(pData,i,right) ;
    }
    
    
    /**
     * Implements quicksort according to Manber's "Introduction to Algorithms".
     * 
     * @param array the array of integers to be sorted
     * @param index the index into the array of integers
     * @param left the first index of the subset to be sorted
     * @param right the last index of the subset to be sorted
     */
    // @ requires 0 <= first && first <= right && right < array.length;
    // @ requires (\forall int i; 0 <= i && i < index.length; 0 <= index[i] &&
    // index[i] < array.length);
    // @ requires array != index;
    // assignable index;
    private static void quickSort(/* @non_null@ */int[] array, /* @non_null@ */
        int[] index, int left, int right) {

      if (left < right) {
        int middle = partition(array, index, left, right);
        quickSort(array, index, left, middle);
        quickSort(array, index, middle + 1, right);
      }
    }
    
    
    /**
     * Implements quicksort with median-of-three method and explicit sort for
     * problems of size three or less.
     * 
     * @param array the array of doubles to be sorted
     * @param index the index into the array of doubles
     * @param left the first index of the subset to be sorted
     * @param right the last index of the subset to be sorted
     */
    // @ requires 0 <= first && first <= right && right < array.length;
    // @ requires (\forall int i; 0 <= i && i < index.length; 0 <= index[i] &&
    // index[i] < array.length);
    // @ requires array != index;
    // assignable index;
    private static void quickSort(/* @non_null@ */double[] array, /* @non_null@ */
        int[] index, int left, int right) {

      int diff = right - left;

      switch (diff) {
      case 0:

        // No need to do anything
        return;
      case 1:

        // Swap two elements if necessary
        conditionalSwap(array, index, left, right);
        return;
      case 2:

        // Just need to sort three elements
        conditionalSwap(array, index, left, left + 1);
        conditionalSwap(array, index, left, right);
        conditionalSwap(array, index, left + 1, right);
        return;
      default:

        // Establish pivot
        int pivotLocation = sortLeftRightAndCenter(array, index, left, right);

        // Move pivot to the right, partition, and restore pivot
        swap(index, pivotLocation, right - 1);
        int center = partition(array, index, left, right, array[index[right - 1]]);
        swap(index, center, right - 1);

        // Sort recursively
        quickSort(array, index, left, center - 1);
        quickSort(array, index, center + 1, right);
      }
    }
    
    /**
     * Sorts left, right, and center elements only, returns resulting center as
     * pivot.
     */
    private static int sortLeftRightAndCenter(double[] array, int[] index, int l,
        int r) {

      int c = (l + r) / 2;
      conditionalSwap(array, index, l, c);
      conditionalSwap(array, index, l, r);
      conditionalSwap(array, index, c, r);
      return c;
    }
    
    /**
     * Partitions the instances around a pivot. Used by quicksort and
     * kthSmallestValue.
     * 
     * @param array the array of doubles to be sorted
     * @param index the index into the array of doubles
     * @param l the first index of the subset
     * @param r the last index of the subset
     * 
     * @return the index of the middle element
     */
    private static int partition(double[] array, int[] index, int l, int r,
        double pivot) {

      r--;
      while (true) {
        while ((array[index[++l]] < pivot))
          ;
        while ((array[index[--r]] > pivot))
          ;
        if (l >= r) {
          return l;
        }
        swap(index, l, r);
      }
    }
    

    /**
     * Swaps two elements in the given integer array.
     */
    private static void swap(int[] index, int l, int r) {

      int help = index[l];
      index[l] = index[r];
      index[r] = help;
    }

    /**
     * Conditional swap for quick sort.
     */
    private static void conditionalSwap(double[] array, int[] index, int left,
        int right) {

      if (array[index[left]] > array[index[right]]) {
        int help = index[left];
        index[left] = index[right];
        index[right] = help;
      }
    }

    /**
     * Partitions the instances around a pivot. Used by quicksort and
     * kthSmallestValue.
     * 
     * @param array the array of integers to be sorted
     * @param index the index into the array of integers
     * @param l the first index of the subset
     * @param r the last index of the subset
     * 
     * @return the index of the middle element
     */
    private static int partition(int[] array, int[] index, int l, int r) {

      double pivot = array[index[(l + r) / 2]];
      int help;

      while (l < r) {
        while ((array[index[l]] < pivot) && (l < r)) {
          l++;
        }
        while ((array[index[r]] > pivot) && (l < r)) {
          r--;
        }
        if (l < r) {
          help = index[l];
          index[l] = index[r];
          index[r] = help;
          l++;
          r--;
        }
      }
      if ((l == r) && (array[index[r]] > pivot)) {
        r--;
      }

      return r;
    }

    
    public static void main(String[] args) {
		
	}
    
    
}