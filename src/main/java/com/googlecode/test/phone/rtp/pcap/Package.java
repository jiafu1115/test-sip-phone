package com.googlecode.test.phone.rtp.pcap;



public class Package {
	 
	private PackageHeader packageHeader;
	private PackageData packageData;
	 
	public Package(PackageHeader packageHeader, PackageData packageData) {
		super();
		this.packageHeader = packageHeader;
		this.packageData = packageData;
	}

	public PackageHeader getPackageHeader() {
		return packageHeader;
	}

	public PackageData getPackageData() {
		return packageData;
	}

	@Override
	public String toString() {
		return "Package [packageHeader=" + packageHeader + ", packageData="
				+ packageData + "]";
	}
	
	
	 
}
