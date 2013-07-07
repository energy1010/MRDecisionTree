package com.younger.tree;

import java.util.List;

public class Tree {
	
	public class TreeData{
		private int data;
		private List<TreeData> children;
		
		public int getData() {
			return data;
		}
		
		public TreeData(){
			
		}
		
		public TreeData(int data){
			this.data = data;
		}
		
		public TreeData(Integer data){
			this.data = data;
		}
		
		public void setData(int data) {
			this.data = data;
		}
		public List<TreeData> getChildren() {
			return children;
		}
		public void setChildren(List<TreeData> children) {
			this.children = children;
		}
		
		public String getChildrenDatas(){
			StringBuffer sb = new StringBuffer();
			int i=0;
			for(TreeData t: children){
				if(i!=0){
					sb.append(",");
				}
				sb.append(t.getData());	
			}
			return sb.toString();
		}
		
		@Override
		public String toString() {
			return "TreeData [data=" + data + ", children=" + getChildrenDatas() + "]";
		}
		
	}
	
	
	public TreeData createTree(List<Integer> dataList){
		TreeData root = new TreeData(dataList.get(0));
//		root.setChildren(children)
		return root;
	}
	
	public static void main(String[] args) {
	}
	
	
	
	

}
