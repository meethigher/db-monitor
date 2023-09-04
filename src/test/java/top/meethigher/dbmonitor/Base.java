package top.meethigher.dbmonitor;

public class Base {
    private String baseName = "base";

    static {
        System.out.println("Base静态代码块执行");
    }

    {
        System.out.println("Base构造块执行");
    }

    public Base() {
        System.out.println("Base构造函数执行");
        callName();
    }

    public void callName() {
        System.out.println(baseName);
    }

    static class Sub extends Base {
        private String baseName = "sub";

        {
            System.out.println("Sub构造块执行");
        }

        static {
            System.out.println("Sub静态代码块执行");
        }

        public Sub() {
            System.out.println("Sub构造函数执行");
        }

        public void callName() {
            System.out.println(baseName);
        }
    }

    public static void main(String[] args) {
        Base b = new Sub();//null
    }
}
