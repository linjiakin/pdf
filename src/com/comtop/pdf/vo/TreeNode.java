package com.comtop.pdf.vo;

import java.io.Serializable;

/**
 * 节点树VO对象
 * @author Administrator
 *
 */
public class TreeNode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nodeId;
	private String nodeTitle;
	private int nodeLevel;
	private boolean isParent;
	private String functionId;
	private String fatherFunctionId;
	private boolean parent;
	/**
	 * 设备类别 :1摄像头、2变压器、3电缆、4开关柜子
	 * 当nodeLevel为4(设备)时，该字段生效
	 */
	private int deviceType;

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeTitle() {
		return nodeTitle;
	}

	public void setNodeTitle(String nodeTitle) {
		this.nodeTitle = nodeTitle;
	}

	public int getNodeLevel() {
		return nodeLevel;
	}

	public void setNodeLevel(int nodeLevel) {
		this.nodeLevel = nodeLevel;
	}

	public boolean setIsParent() {
		return isParent;
	}

	public void getIsParent(boolean isParent) {
		this.isParent = isParent;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public String getFatherFunctionId() {
		return fatherFunctionId;
	}

	public void setFatherFunctionId(String fatherFunctionId) {
		this.fatherFunctionId = fatherFunctionId;
	}

	public boolean isParent() {
		return parent;
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}
}
