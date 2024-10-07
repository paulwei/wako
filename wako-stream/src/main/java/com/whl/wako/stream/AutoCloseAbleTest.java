package com.whl.wako.stream;

public class AutoCloseAbleTest implements AutoCloseable{

    public void func(){
        System.out.println(" func 执行 ");
    }
    @Override
    public void close() throws Exception {
        System.out.println("关闭资源close()");
    }

    public static void main(String[] args) {

        try(AutoCloseAbleTest test=new AutoCloseAbleTest()){
                test.func();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
