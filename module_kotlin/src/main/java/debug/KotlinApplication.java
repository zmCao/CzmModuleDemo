package debug;

import com.czm.module.common.base.BaseApplication;

/**
 * <p>类说明</p>
 *
 * @author 曹志敏 2018/3/30
 * @version V1.0.0
 * @name KotlinApplication
 */
public class KotlinApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        login();
    }

    /**
     * 在这里模拟登陆，然后拿到sessionId或者Token
     * 这样就能够在组件请求接口了
     */
    private void login() {

    }
}
