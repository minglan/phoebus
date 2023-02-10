
package org.phoebus.applications.alarm.ui.annunciator;

import org.phoebus.ui.javafx.PlatformInfo;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

/**
 * Annunciator class. Uses freeTTS to annunciate passed messages.
 * 
 * @author Evan Smith
 */
@SuppressWarnings("nls")
public class AnnunciatorMS {
	private final VoiceManager voiceManager;
	private final Voice voice;
	private static final String voice_name = "kevin16";
	 

	public AnnunciatorMS() {
		// Define the voices directory.
		System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
		voiceManager = VoiceManager.getInstance();
		voice = voiceManager.getVoice(voice_name);
		voice.allocate();

	}

	/**
	 * Annunciate the message. Only returns once speaking finishes.
	 * 
	 * @param message
	 */
	public void speakLinuxOrMac(final String message) {
		if (null != message)
			voice.speak(message);
	}

	public void speakWindows(String message) {
		ActiveXComponent sap = new ActiveXComponent("Sapi.SpVoice");
		if (null != message)
		try {
		// 音量 0-100
		sap.setProperty("Volume", new Variant(100));
		// 语音朗读速度 -10 到 +10
		sap.setProperty("Rate", new Variant(2));

			// 获取执行对象
			Dispatch sapo = sap.getObject();
			// 执行朗读
			Dispatch.call(sapo, "Speak", new Variant(message));
			// 关闭执行对象
			sapo.safeRelease();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭应用程序连接
			sap.safeRelease();
		}
	}
	
	
	public void speak(String message) {
		if(PlatformInfo.isWindows)speakWindows( message);
		else speakLinuxOrMac(message) ;
		
	}

	/**
	 * Deallocates the voice.
	 */
	public void shutdown() {
		voice.deallocate();
	}
}
