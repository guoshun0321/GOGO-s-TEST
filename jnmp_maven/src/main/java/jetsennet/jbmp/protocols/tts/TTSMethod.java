package jetsennet.jbmp.protocols.tts;

/**
 * JNI调用MS-speechsdk5.1
 * @author xuyuji
 *
 */
public class TTSMethod
{
    static
    {
        //装载JnmpCatvTTS.DLL,这个DLL在版本说明中，
        //运行代码前将该DLL拷贝到window/System32/下。
        System.loadLibrary("JbmpTTS");
    }

    /**
     * 朗读,默认语速0音量100
     * @param words 话
     * @return
     */
    public native int speak(String words);

    /**
     * 朗读
     * @param words	话
     * @param rate	语速,范围[-10,10]
     * @param volume音量,范围[0,100]
     * @return
     */
    public native int speak(String words, int rate, int volume);

    public static void main(String[] args)
    {
        TTSMethod ts = new TTSMethod();

        String words = "服务器爆炸啦!" + "tomcat奔溃啦！" + "服务器被修空调的拉走啦！" + "机房着火啦！";

        ts.speak(words);
        ts.speak(words, 5, 100);

        String poem = "惨惨惨！" + "吃晚饭要拉，" + "拉完还要撒，" + "撒完还想吃，" + "人生就是吃拉撒！";
        ts.speak(poem, -4, 100);

        String aTongueTwister =
            "白石塔，白石搭，白石搭白塔，白塔白石搭， 搭好白石塔，白塔白又大 。" + "吃葡萄不出葡萄皮，不吃葡萄倒吐葡萄皮。" + "四和十，十和四，十四和四十，四十和十四。" + "八百标兵奔北坡，北坡炮兵并排跑，炮兵怕把标兵碰，标兵怕碰炮兵炮。 ";
        ts.speak(aTongueTwister, 5, 100);
    }
}
