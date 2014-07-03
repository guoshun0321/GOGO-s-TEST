package jetsennet.jbmp.manage;

public interface ILifecycle
{

    public void start();

    public void stop();
    
    public String getState();
    
    public String getStateDesc();

}
