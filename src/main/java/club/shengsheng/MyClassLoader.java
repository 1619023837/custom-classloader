package club.shengsheng;


import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class MyClassLoader extends ClassLoader {

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            //已经加载过 就不在执行了
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                if (name.startsWith("tech")) {
                    return define(new File("加密.class"), name);
                }else {
                    return getParent().loadClass(name);
                }
            }
            return c;
        }

    }
    private Class<?> define(File path, String name ) throws ClassNotFoundException{
        //堆内缓冲区
        ByteBuffer buffer = ByteBuffer.allocate((int) path.length());
        try (FileChannel channel = new FileInputStream(path).getChannel()) {
            ByteBuffer tempBuff = ByteBuffer.allocate(30);
            while (channel.read(tempBuff) != -1) {
                tempBuff.flip();
                while (tempBuff.hasRemaining()) {
                    byte b = tempBuff.get();
                    b = (byte) (b-1);
                    buffer.put(b);
                }
                tempBuff.clear();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Class<?> result =  super.defineClass(name,buffer.array(),0,buffer.limit());
        if (result == null) {
            throw new ClassNotFoundException(name);
        }
        return result;

    }
}

