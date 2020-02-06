package com.example.nianiadladziecka;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.JSch;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

public class SShCommandSend {
    public static String executeRemoteCommand(String username, String password, String hostname, int port, String command)
            throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        session.connect();

        // SSH Channel
        ChannelExec channelssh = (ChannelExec)
                session.openChannel("exec");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        channelssh.setOutputStream(baos);

        // Execute command

        channelssh.setCommand(command);
        channelssh.connect();
        channelssh.disconnect();

        return baos.toString();
    }
}
