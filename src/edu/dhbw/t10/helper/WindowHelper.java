/*
 * *********************************************************
 * Copyright (c) 2011 - 2012, DHBW Mannheim
 * Project: T10 On-Screen Keyboard
 * Date: Feb 4, 2012
 * Author(s): NicolaiO
 * 
 * *********************************************************
 */
package edu.dhbw.t10.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

import edu.dhbw.t10.manager.output.Output;


/**
 * Helper class for getting information about external windows.
 * This is usually OS dependent.
 * 
 * @author NicolaiO
 * 
 */
public class WindowHelper {
	// --------------------------------------------------------------------------
	// --- variables and constants ----------------------------------------------
	// --------------------------------------------------------------------------
	private static final Logger	logger	= Logger.getLogger(WindowHelper.class);
	
	
	// --------------------------------------------------------------------------
	// --- constructors ---------------------------------------------------------
	// --------------------------------------------------------------------------
	
	
	// --------------------------------------------------------------------------
	// --- methods --------------------------------------------------------------
	// --------------------------------------------------------------------------
	public static String getActiveWindowTitle() {
		if (Output.getOs() == Output.LINUX) {
			try {
				String command[] = {
						"bash",
						"-c",
						"ps -p $(xprop -id `xprop -root | awk '/_NET_ACTIVE_WINDOW\\(WINDOW\\)/{print $NF}'` | awk '/_NET_WM_PID\\(CARDINAL\\)/{print $NF}') -o cmd=" };
				Process pr = Runtime.getRuntime().exec(command);
				
				int exitVal = pr.waitFor();
				
				BufferedReader input;
				String line = null;
				if (exitVal == 0) {
					input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
					line = input.readLine();
					if (line == null) {
						logger.error("Could not get active window title");
					} else {
						return line;
					}
				} else {
					input = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
					while ((line = input.readLine()) != null) {
						logger.error(line);
					}
				}
			} catch (IOException err) {
				err.printStackTrace();
			} catch (InterruptedException err) {
				err.printStackTrace();
			}
		} else if (Output.getOs() == Output.WINDOWS) {
			
		} else {
			logger.error("Operating System not supported");
		}
		return "";
	}
	
	private static void getActiveTitleWindows() {
		final List<WindowInfo> inflList = new ArrayList<WindowInfo>();
		final List<Integer> order = new ArrayList<Integer>();
		int top = User32.instance.GetTopWindow(0);
		while (top != 0) {
			order.add(top);
			top = User32.instance.GetWindow(top, User32.GW_HWNDNEXT);
		}
		User32.instance.EnumWindows(new WndEnumProc() {
			public boolean callback(int hWnd, int lParam) {
				if (User32.instance.IsWindowVisible(hWnd)) {
					RECT r = new RECT();
					User32.instance.GetWindowRect(hWnd, r);
					if (r.left > -32000) { // minimized
						byte[] buffer = new byte[1024];
						User32.instance.GetWindowTextA(hWnd, buffer, buffer.length);
						String title = Native.toString(buffer);
						inflList.add(new WindowInfo(hWnd, r, title));
					}
				}
				return true;
			}
		}, 0);
		Collections.sort(inflList, new Comparator<WindowInfo>() {
			public int compare(WindowInfo o1, WindowInfo o2) {
				return order.indexOf(o1.hwnd) - order.indexOf(o2.hwnd);
			}
		});
		for (WindowInfo w : inflList) {
			System.out.println(w);
		}
	}
	
	public static interface WndEnumProc extends StdCallLibrary.StdCallCallback {
		boolean callback(int hWnd, int lParam);
	}
	
	public static interface User32 extends StdCallLibrary {
		final User32	instance	= (User32) Native.loadLibrary("user32", User32.class);
		
		
		boolean EnumWindows(WndEnumProc wndenumproc, int lParam);
		
		
		boolean IsWindowVisible(int hWnd);
		
		
		int GetWindowRect(int hWnd, RECT r);
		
		
		void GetWindowTextA(int hWnd, byte[] buffer, int buflen);
		
		
		int GetTopWindow(int hWnd);
		
		
		int GetWindow(int hWnd, int flag);
		
		final int	GW_HWNDNEXT	= 2;
	}
	
	public static class RECT extends Structure {
		public int	left, top, right, bottom;
	}
	
	public static class WindowInfo {
		int		hwnd;
		RECT		rect;
		String	title;
		
		
		public WindowInfo(int hwnd, RECT rect, String title) {
			this.hwnd = hwnd;
			this.rect = rect;
			this.title = title;
		}
		
		
		public String toString() {
			return String.format("(%d,%d)-(%d,%d) : \"%s\"", rect.left, rect.top, rect.right, rect.bottom, title);
		}
	}

	// --------------------------------------------------------------------------
	// --- getter/setter --------------------------------------------------------
	// --------------------------------------------------------------------------
}
