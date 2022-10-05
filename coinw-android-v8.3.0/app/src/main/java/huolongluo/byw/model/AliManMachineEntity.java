package huolongluo.byw.model;
import java.io.Serializable;
//阿里人机验证成功返回的数据
public class AliManMachineEntity implements Serializable {
    private String nvcSessionId;
    private String nvcSig;
    private String nvcToken;
    private String nvcScene;

    public AliManMachineEntity(String nvcSessionId, String nvcSig, String nvcToken, String nvcScene) {
        this.nvcSessionId = nvcSessionId;
        this.nvcSig = nvcSig;
        this.nvcToken = nvcToken;
        this.nvcScene = nvcScene;
    }

    public String getNvcSessionId() {
        return nvcSessionId;
    }

    public void setNvcSessionId(String nvcSessionId) {
        this.nvcSessionId = nvcSessionId;
    }

    public String getNvcSig() {
        return nvcSig;
    }

    public void setNvcSig(String nvcSig) {
        this.nvcSig = nvcSig;
    }

    public String getNvcToken() {
        return nvcToken;
    }

    public void setNvcToken(String nvcToken) {
        this.nvcToken = nvcToken;
    }

    public String getNvcScene() {
        return nvcScene;
    }

    public void setNvcScene(String nvcScene) {
        this.nvcScene = nvcScene;
    }
}
