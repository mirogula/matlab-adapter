package sk.stuba.fei.jlab.matlabadapter;
//package sk.stuba.fei.jlab.matlabadapter;
//
//import java.io.File;
//import java.io.IOException;
//
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//import javax.servlet.annotation.WebListener;
//
///**
// * Application Lifecycle Listener implementation class PropertiesLoader
// *
// */
//@WebListener
//public class PropertiesLoader implements ServletContextListener {
//
//	/**
//     * @see ServletContextListener#contextInitialized(ServletContextEvent)
//     */
//	@Override
//    public void contextInitialized(ServletContextEvent event) {
//        String propertiesFile = "MatlabAdapter.properties";
//        System.out.println("CONTEXT LISTENER TEST !!!!!!!!!!!!!! :::  "+new File(propertiesFile).getAbsolutePath());
//        try {
//			MatlabAdapterProperties.load(propertiesFile);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//    }
//
//	/**
//     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
//     */
//    @Override
//    public void contextDestroyed(ServletContextEvent event) {
//        // not implemented
//    }
//	
//}
