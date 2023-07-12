package com.example.websitesurfingbot;

import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.ReCaptcha;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class reCaptchaSolver {

    Properties properties = PropertiesInitialize.getProperties();
    TwoCaptcha solver = new TwoCaptcha(properties.getProperty("rucaptha.api.key"));
    ReCaptcha captcha = new ReCaptcha();

    public void setupCaptcha() {
        captcha.setSiteKey("6LeuU18hAAAAAIWW2pbr8TlkvIBWI9FsBVhxumQW");
        captcha.setUrl("https://socpublic.com/auth_login.html");
        captcha.setInvisible(true);
        captcha.setAction("verify");
    }

    public String solveCuptcha(){
        String code = "";
        try {
            String captchaId = solver.send(captcha);
            while (true){
                if(solver.getResult(captchaId)!=null){
                    break;
                }
            }
            code = solver.getResult(captchaId);
        }catch (Exception exc){
            System.out.println(Thread.currentThread().getName() + "Ошибка решения капчи");
            exc.printStackTrace();
        }
        return code;
    }
}
