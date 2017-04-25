package sample;

/**
 * Created by Akib Jawad on 25-Apr-17.
 */
public class TileOrDetais
{
    public static TileOrDetais instance=new TileOrDetais();
    public String mode="Details";
    private TileOrDetais(){};
    public static TileOrDetais getInstance(){return instance;}
}
