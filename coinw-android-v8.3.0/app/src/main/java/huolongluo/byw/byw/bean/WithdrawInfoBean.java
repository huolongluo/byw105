package huolongluo.byw.byw.bean;


import java.io.Serializable;

public class WithdrawInfoBean implements Serializable
{


            /*"address": "广东省广州市荔湾区",
            "bankNumber": "交通银行 尾号5554",
            "bankType": 4,
            "id": 22590,
            "name": "交通银行"*/


    private int id = 0;
    private String bankType = "";
    private String address = "";
    private String bankNumber = "";
    private String name = "";

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getBankType()
    {
        return bankType;
    }

    public void setBankType(String bankType)
    {
        this.bankType = bankType;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getBankNumber()
    {
        return bankNumber;
    }

    public void setBankNumber(String bankNumber)
    {
        this.bankNumber = bankNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "WithdrawInfoBean{" +
                "id=" + id +
                ", bankType='" + bankType + '\'' +
                ", address='" + address + '\'' +
                ", bankNumber='" + bankNumber + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
