package com.yuriylisovskiy.er.Util;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	private static final String APP_NAME = "EventReminder";

	private static String _logPath;
	private static Logger _instance;

	private Logger() {}

	public static Logger getInstance() {
		if (Logger._logPath == null) {
			Logger._logPath = Environment.getExternalStorageDirectory() + "/" + Logger.APP_NAME + "/";
		}
		if (Logger._instance == null) {
			Logger._instance = new Logger();
		}
		return Logger._instance;
	}

	public void error(String msg) {
		this.log("error", msg);
	}

	public void info(String msg) {
		this.log("info", msg);
	}

	public void debug(String msg) {
		this.log("debug", msg);
	}

	private void log(String tag, String msg) {
		try {
			if (Logger.ensureDirExists()) {
				File logFile = Logger.getFile(tag);
				if (logFile != null) {
					BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
					buf.append("[").append(String.valueOf(new java.sql.Timestamp(System.currentTimeMillis()))).append("]: ").append(msg);
					buf.newLine();
					buf.newLine();
					buf.close();
				}
			} else {
				throw new IOException("unable to create directory for logging");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static File getFile(String tag) throws IOException {
		File file = new File(Logger._logPath + tag + ".log");
		if (file.exists()) {
			return file;
		}
		if (file.createNewFile()) {
			BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
			buf.append(" =========== " + Logger.APP_NAME + " - LOG ==========");
			buf.newLine();
			buf.close();
			return file;
		}
		return null;
	}

	private static boolean ensureDirExists() {
		File folder = new File(Logger._logPath);
		boolean success = true;
		if (!folder.exists()) {
			success = folder.mkdir();
		}
		return success;
	}
}
