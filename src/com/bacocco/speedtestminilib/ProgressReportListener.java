package com.bacocco.speedtestminilib;

public interface ProgressReportListener {
	public void reportCurrentDownloadSpeed(long bits);
	public void reportCurrentUploadSpeed(long bits);
}
