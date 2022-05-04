package icu.whereis.javaclipboard;

import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class Main {
    public static void main(String[] args) {
        monitorClipboard();
    }

    /**
     * 监控系统剪切板
     */
    public static void monitorClipboard() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean flag = false;
                    String lastStr = "";

                    while (true) {
                        Thread.sleep(500);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        Transferable contents = clipboard.getContents(null);
                        if (contents != null) {
                            if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                                try {
                                    String text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                                    System.out.println("获取到内容：" + text);
                                    if (StringUtils.isNotBlank(text)) {
                                        if (text.startsWith("rtmp://")) {
                                            if (Objects.equals(lastStr, text)) {
                                                System.out.println("检测字符串已替换，不操作");
                                                continue;
                                            }

                                            lastStr = text;
                                            if (text.contains("<playpath>")) {
                                                text = text.replaceAll("<playpath>", "/");
                                                if (text.contains("<swfUrl>")) {
                                                    text = text.replaceAll("<swfUrl>", "");
                                                    if (text.contains("<pageUrl>")) {
                                                        text = text.replaceAll("<pageUrl>", "");
                                                    }
                                                }
                                            }

                                            StringSelection selection = new StringSelection(text.trim());
                                            clipboard.setContents(selection, null);
                                            System.out.println("替换后内容：" + text);
                                        }
                                    }
                                } catch (UnsupportedFlavorException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
