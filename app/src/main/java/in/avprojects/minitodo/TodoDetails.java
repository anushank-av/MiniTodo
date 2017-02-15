package in.avprojects.minitodo;

/**
 * Created by anush on 15-02-2017.
 */

public class TodoDetails {
    private int tPriority;
    private String tTitle;
    public TodoDetails(int mPriority,String mTitle){
        tPriority = mPriority;
        tTitle = mTitle;
    }
    public int getId(){return tPriority;}
    public String gettTitle(){return tTitle;}
}