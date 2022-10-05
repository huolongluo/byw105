package huolongluo.byw.byw.injection.component;


import dagger.Component;
import huolongluo.byw.base.PerActivity;
import huolongluo.byw.byw.injection.model.ActivityModule;
import huolongluo.byw.byw.ui.activity.AdvertActivity;
import huolongluo.byw.byw.ui.activity.UpVersionActivity;
import huolongluo.byw.byw.ui.activity.addbankcard.AddBankCardActivity;
import huolongluo.byw.byw.ui.activity.bancard.BankCardListActivity;
import huolongluo.byw.byw.ui.activity.banklist.BankListActivity;
import huolongluo.byw.byw.ui.activity.bindemail.BindEmailActivity;
import huolongluo.byw.byw.ui.activity.bindphone.BindPhoneActivity;
import com.android.legend.ui.login.LoginActivity;
import huolongluo.byw.byw.ui.activity.main.MainActivity;
import huolongluo.byw.byw.ui.activity.renzheng.CountryActiivty;
import huolongluo.byw.byw.ui.activity.renzheng.RenZhengInfoActivity;
import huolongluo.byw.byw.ui.activity.rmbtixian.RmbTiXianActivity;
import huolongluo.byw.byw.ui.activity.safe_centre.SafeCentreActivity;
import huolongluo.byw.byw.ui.activity.setchangepsw.SetChangePswActivity;
import huolongluo.byw.byw.ui.fragment.contractTab.ConTractFinanceFragment;
import huolongluo.byw.byw.ui.fragment.maintab03.FinanceFragment;
import huolongluo.byw.byw.ui.fragment.maintab04.MineFragment;
import huolongluo.byw.reform.mine.activity.FinanceActivity;
/**
 * <p>
 * Created by 火龙裸 on 2017/8/21.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(LoginActivity activity);
    void inject(FinanceActivity activity);
    void inject(FinanceFragment fragment);
    void inject(MineFragment fragment);
    void inject(AdvertActivity activity);
    void inject(RmbTiXianActivity activity);
    void inject(CountryActiivty activity);
    void inject(BankCardListActivity activity);
    void inject(AddBankCardActivity activity);
    void inject(BankListActivity activity);
    void inject(RenZhengInfoActivity activity);
    void inject(UpVersionActivity activity);
    void inject(SafeCentreActivity activity);
    void inject(SetChangePswActivity activity);
    void inject(BindEmailActivity activity);
    void inject(BindPhoneActivity activity);
    void inject(ConTractFinanceFragment conTractFinanceFragment);
}
