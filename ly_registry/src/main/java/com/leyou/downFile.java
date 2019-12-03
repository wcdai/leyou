//package com.leyou;
//
//import org.apache.commons.net.ftp.FTPClient;
//import org.apache.commons.net.ftp.FTPClientConfig;
//import org.apache.commons.net.ftp.FTPFile;
//import org.apache.commons.net.ftp.FTPReply;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class downFile {
//    public static void main(String[] args) {
//
//        System.out.println(new Date()+"  开始进入ftpDownload定时器");
//
//        //ftp服务器登录凭证
//        String host=PropertiesManager.getProperty("ftpHost");
//        int port=Integer.parseInt(PropertiesManager.getProperty("ftpPort"));
//        String user=PropertiesManager.getProperty("ftpUser");
//        String password=PropertiesManager.getProperty("ftpPassword");
//
//        //获取时间字段信息
//        SimpleDateFormat sdf1=new SimpleDateFormat("yyyyMMdd");
//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//        Date date=new Date();
//        String today1 = sdf1.format(date);
//        String today = sdf.format(date);
//
//        String txtFileDir="/";
//        String txtSaveDir="E:/dataCenter/shengzhan/";
//
//        //检查本地磁盘目录是否存在txt文件
//        boolean flag = isTxtExit(today1,txtSaveDir);
//        System.out.println(new Date()+"  判断txt文件是否存在："+flag);
//        FlagUtil.ftpDownloadRunning=true;
//
//        //讲txt的下载操作和解析操作分成2个独立的操作进行，排除互相间的干扰
//        if(flag==false)//文件不存在进行ftp下载操作
//        {
//            FTPClient ftp=null;
//            try
//            {
//                //ftp的数据下载
//                ftp=new FTPClient();
//                ftp.connect(host, port);
//                ftp.login(user, password);
//                ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
//
//                //设置linux环境
//                FTPClientConfig conf = new FTPClientConfig( FTPClientConfig.SYST_UNIX);
//                ftp.configure(conf);
//
//                //判断是否连接成功
//                int reply = ftp.getReplyCode();
//                if (!FTPReply.isPositiveCompletion(reply))
//                {
//                    ftp.disconnect();
//                    System.out.println("FTP server refused connection.");
//                    return;
//                }
//
//                //设置访问被动模式
//                ftp.setRemoteVerificationEnabled(false);
//                ftp.enterLocalPassiveMode();
//
//
//                //检索ftp目录下所有的文件，利用时间字符串进行过滤
//                boolean dir = ftp.changeWorkingDirectory(txtFileDir);
//                if (dir)
//                {
//                    FTPFile[]fs = ftp.listFiles();
//                    for(FTPFile f:fs)
//                    {
//                        if(f.getName().indexOf(today1+"2000")>0)
//                        {
//                            System.out.println(new Date()+"  ftpDownload定时器下载txt成功");
//                            File localFile = new File(txtSaveDir+f.getName());
//                            OutputStream ios = new FileOutputStream(localFile);
//                            ftp.retrieveFile(f.getName(), ios);
//                            ios.close();
//                            break;
//                        }
//                    }
//                }
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//                System.out.println(new Date()+"  ftp下载txt文件发生错误");
//            }
//            finally
//            {
//
//                if(ftp != null)  {
//                    try {ftp.disconnect();} catch (IOException ioe) {}
//                }
//            }
//
//    }
//
//
//   /* *//**从FTP下载文件*//*
//    public static List<PrpJFOrder> downFile(String url, int port,String username, String password,
//                                            String fileName,String localPath,String movePath,String ftpPath){//fileName:用来指定某些文件  movePath:这里是用来备份文件的路径,正常场景下都需要备份  localPath:下载下来的路径,一般解析完之后就复制到备份文件的路径然后删除   ftpPath:一般FTP服务器会指向一个根目录,而我们的文件不一定在根目录,所以需要到某个路径下寻找文件
//        FTPClient ftp = new FTPClient();//创建连接FTP类,这个类需要commons-net-3.3.jar包的支持
//        List<PrpJFOrder> readFile = null;//这是我要解析的文件需要存到某个对象中,无须关注
//        try {
//            int reply;
//            ftp.connect(url,port);//连接端口与IP
//            if(!ftp.login(username, password)){
//                System.out.println("登陆FTP用户或密码错误");//登陆
//            }
//            reply = ftp.getReplyCode();
//            if(!FTPReply.isPositiveCompletion(reply)){
//                ftp.disconnect();//连接失败
//                System.out.println("连接失败");
//                return null;
//            }
//            ftp.changeWorkingDirectory(ftpPath);//指定FTP文件目录
//            //转移到FTP服务器目录
//            FTPFile[] fs = ftp.listFiles();//服务器目录下所有文件
//            for(FTPFile ff:fs){//遍历这些文件查找符合条件的文件
//                if(ff.getName().indexOf(fileName)!=-1 && ff.getName().indexOf("yx")!=-1){//遍历到当前文件如果包含日期字符串
//                    File localFile = new File(localPath+File.separator+ff.getName());
//                    OutputStream os = new FileOutputStream(localFile);
//                    ftp.retrieveFile(ff.getName(), os);
//                    //解析文件下载下来的文件
//                    readFile = readFile(localPath+File.separator+ff.getName());
//                    //解析完毕将文件移到指定目录-->复制+删除
//                    if(localFile.exists()){
//                        File file = new File(movePath+File.separator+ff.getName());//移动的目录
//                        FileInputStream fis = new FileInputStream(localFile);
//                        FileOutputStream fos = new FileOutputStream(file);
//                        byte[] buff = new byte[1024];
//                        int length;
//                        while((length=fis.read(buff))>0){
//                            fos.write(buff, 0, length);
//                        }
//                        //删除原文件
//                        fis.close();
//                        fos.close();
//                    }
//                    if(os!=null){
//                        os.close();
//                    }
//                    localFile.delete();
//                }
//            }
//            ftp.logout();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally{
//            if(ftp.isConnected()){
//                try{
//                    ftp.disconnect();
//                }catch(IOException ioe){
//                    ioe.printStackTrace();
//                }
//            }
//        }
//        return readFile;
//    }*/
//
//
//}
