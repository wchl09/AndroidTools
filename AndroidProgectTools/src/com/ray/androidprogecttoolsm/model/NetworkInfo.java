package com.ray.androidprogecttoolsm.model;

/**
 * 网络信息类
 */
public class NetworkInfo {
	
	/**是否可以联网*/
	private boolean isConnectToNetwork = true;
	/**是否需要代理*/
	private boolean isProxy = false;
	/**接入点名称*/
	private String proxyName = "";
	/**代理*/
	private String proxyHost = "";
	/**端口号*/
	private int proxyPort = 0;
	
	/**
	 * 是否可以联网
	 * @return true：可以联网，false 不可以
	 */
	public boolean isConnectToNetwork() {
		return isConnectToNetwork;
	}
	
	/**
	 * 设置是否可以联网
	 * @param isConnectToNetwork 是否可以联网（true：可以联网，false 不可以）
	 */
	public void setConnectToNetwork(boolean isConnectToNetwork) {
		this.isConnectToNetwork = isConnectToNetwork;
	}
	
	/**
	 * 是否需要代理
	 * @return true：需要，false：不需要
	 */
	public boolean isProxy() {
		return isProxy;
	}
	
	/**
	 * 设置是否需要代理
	 * @param isProxy 是否需要代理 （true：需要，false：不需要）
	 */
	public void setProxy(boolean isProxy) {
		this.isProxy = isProxy;
	}
	
	/**
	 * 获取接入点名称
	 * @return 接入点名称
	 */
	public String getProxyName() {
		return proxyName;
	}
	
	/**
	 * 设置接入点名称
	 * @param proxyName 接入点名称
	 */
	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}
	
	/**
	 * 获取代理
	 * @return 代理
	 */
	public String getProxyHost() {
		return proxyHost;
	}
	
	/**
	 * 设置代理
	 * @param proxyHost 代理
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}
	
	/**
	 * 获取端口号
	 * @return 端口号
	 */
	public int getProxyPort() {
		return proxyPort;
	}
	
	/**
	 * 设置端口号
	 * @param proxyPort 端口号
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

}
