package huolongluo.byw.byw.ui.fragment.contractTab;

import java.util.List;

public class ContractEntity {

    /**
     * result : true
     * code : 0
     * value : 操作成功
     * data : [{"contractId":1,"contractName":"BTCUSDT","contractSize":1.0E-4},{"contractId":2,"contractName":"ETHUSDT","contractSize":0.01},{"contractId":6,"contractName":"EOSUSDT","contractSize":0.1},{"contractId":14,"contractName":"LTCUSDT","contractSize":0.01},{"contractId":15,"contractName":"BCHUSDT","contractSize":0.001},{"contractId":34,"contractName":"BSVUSDT","contractSize":0.001},{"contractId":19,"contractName":"TRXUSDT","contractSize":1},{"contractId":13,"contractName":"ETCUSDT","contractSize":0.1},{"contractId":17,"contractName":"XRPUSDT","contractSize":1},{"contractId":10,"contractName":"BTCUSDT [BTC]","contractSize":1},{"contractId":11,"contractName":"ETHUSDT [ETH]","contractSize":1}]
     */

    private boolean result;
    private int code;
    private String value;
    private List<DataBean> data;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * contractId : 1
         * contractName : BTCUSDT
         * contractSize : 1.0E-4
         */

        private int contractId;
        private String contractName;
        private double contractSize;

        public int getContractId() {
            return contractId;
        }

        public void setContractId(int contractId) {
            this.contractId = contractId;
        }

        public String getContractName() {
            return contractName;
        }

        public void setContractName(String contractName) {
            this.contractName = contractName;
        }

        public double getContractSize() {
            return contractSize;
        }

        public void setContractSize(double contractSize) {
            this.contractSize = contractSize;
        }
    }
}
