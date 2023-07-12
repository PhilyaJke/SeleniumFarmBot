package com.example.websitesurfingbot;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.DevToolsException;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class LoginAccounts implements Runnable {

    private WebDriver driver;
    private final String username;
    private final String password;
    private final String proxyIP;
    private final String proxyUsername;
    private final String proxyPassword;
    private static int counter = 0;

    public LoginAccounts(String username, String password, String proxyIP, String proxyUsername, String proxyPassword) {
        this.username = username;
        this.password = password;
        this.proxyIP = proxyIP;
        this.proxyUsername = proxyUsername;
        this.proxyPassword = proxyPassword;
    }

    @Override
    public void run() {
        driver = setup();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100000L));
        login(driver, wait);
        wait.until(ExpectedConditions.urlToBe("https://socpublic.com/account/"));
        driver.navigate().to("https://socpublic.com/account/visit.html?type=redirect&page_limit=100&page=1");
        wait.until(ExpectedConditions.urlToBe("https://socpublic.com/account/visit.html?type=redirect&page_limit=100&page=1"));
        try {
            todo(driver, wait);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WebDriver setup(){
            Proxy proxy = new Proxy();
            proxy.setHttpProxy(proxyIP);
            proxy.setSslProxy(proxyIP);
            proxy.setProxyType(Proxy.ProxyType.MANUAL);
            proxy.setSocksPassword(proxyPassword);
            proxy.setSocksUsername(proxyUsername);
            ChromeOptions options = new ChromeOptions();
            System.setProperty("webdriver.chrome.driver", BotStartingClass.properties.getProperty("chrome.driver"));
            System.setProperty("webdriver.chrome.silentOutput", "true");
            Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--disable-logging");
            options.addArguments("--disable-popup-blocking");
            options.setHeadless(true);
            options.setProxy(proxy);
            WebDriver driver = new ChromeDriver(options);
            driver.manage().window().setSize(new Dimension(600, 250));
            DevTools devTools = ((HasDevTools) driver).getDevTools();
            devTools.createSession();
            driver = new Augmenter().
                    addDriverAugmentation("chrome", HasAuthentication.class, (caps, exec) -> (whenThisMatches, useTheseCredentials) -> devTools.getDomains().network().addAuthHandler(whenThisMatches, useTheseCredentials)).augment(driver);
            ((HasAuthentication) driver).register(UsernameAndPassword.of(proxyUsername, proxyPassword));
        return driver;
    }

    public void login(WebDriver driver, WebDriverWait wait) {
        System.out.println(Thread.currentThread().getName() + " начал вход на сайт");
        driver.navigate().to("https://socpublic.com/auth_login.html");
        try {
            if (driver.findElement(By.xpath("//*[@id=\"cf-error-details\"]/header/h2")).getText().equals("Access denied")) {
                System.out.println(Thread.currentThread().getName() + " доступ на сайт запрещен, закрытие сессии");
                Thread.currentThread().sleep(10000L);
                driver.quit();
                Thread.currentThread().interrupt();
                Thread.currentThread().stop();
            }
        }catch (Exception exc){}
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.name("name")));
        driver.findElement(By.name("name")).sendKeys(username);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.name("password")));
        driver.findElement(By.name("password")).sendKeys(password);
        var reCaptchaSolver = new reCaptchaSolver();
        reCaptchaSolver.setupCaptcha();
        var code = reCaptchaSolver.solveCuptcha();
        System.out.println(Thread.currentThread().getName() + " решил капчу: " + code);
        var element = driver.findElement(By.id("g-recaptcha-response"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", element);
        element.sendKeys(code);
        driver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/div/div[2]/form/div/div[4]/div/button")).click();
    }

    public static List<WebElement> getTasks(WebDriver driver, WebDriverWait wait){
        List<WebElement> webElements = new ArrayList<>();
        try {
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("visit-redirect")));
            webElements.addAll(driver.findElements(By.className("additional-title")));
            driver.navigate().to("https://socpublic.com/account/visit.html?type=redirect&page_limit=100&page=2");
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className("visit-redirect")));
            webElements.addAll(driver.findElements(By.className("additional-title")));
        }catch (NoSuchElementException | TimeoutException exc){
            System.out.println(Thread.currentThread().getName() + " ошибка поиска заданий на страницах (стандартная ошибка)");
        }catch (DevToolsException exc){
            System.out.println("Ошибка devtools при переходе на новую страницу");
        }
        System.out.println(Thread.currentThread().getName() + " заданий найдено: " + webElements.size());
        driver.navigate().to("https://socpublic.com/account/visit.html?type=redirect&page_limit=100&page=1");
        return webElements;
    }

    public static void todo(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        driver.navigate().refresh();
        List<WebElement> webElements = getTasks(driver, wait);
        System.out.println(webElements);
        try {
            for (WebElement webElement : webElements) {
                counter++;
                wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div[3]/div[1]/div[2]/div/div[2]/div/div[1]/div/div/div/div[2]/div/div/div[1]")));
                var substring = Arrays.stream(driver.findElement(By.xpath("/html/body/div[3]/div[1]/div[2]/div/div[2]/div/div[1]/div/div/div/div[2]/div/div/div[1]/div[1]/div[1]/span")).getText().split(" ")).collect(Collectors.toList()).get(0);
                var id = substring.substring(1);
                String xpath = "//*[@id=\"visit_".concat(id).concat("\"]/div[1]/div[1]/a");
                driver.findElement(By.xpath(xpath)).click();
                Thread.sleep(8000L);
                ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
                driver.switchTo().window(tabs.get(1)).close();
                driver.switchTo().window(tabs.get(0));
                driver.navigate().refresh();
                System.out.println(Thread.currentThread().getName() + " вылнил задание " + id + ". Всего задание выполненно: " + counter);
            }
            System.out.println(Thread.currentThread().getName() + " Завершил выполнение всех заданий и завершает сессию. Баланс: ");
            driver.quit();
            Thread.currentThread().interrupt();
            Thread.currentThread().stop();
        }catch (ElementClickInterceptedException | TimeoutException | DevToolsException exc){
            System.out.println("Ошибка выполнения потока: "
                    + Thread.currentThread().getName() + ". Перезапуск метода");
            todo(driver, wait);
        }
    }
}

public class BotStartingClass {

    public static final Properties properties = PropertiesInitialize.getProperties();

    public static void main(String[] args) {

        List<List<String>> accounts = getAccountsInfo();
        List<List<Thread>> threads = new ArrayList<>();
        for (int i = 0; i <= accounts.size()-3; i+=3) {
            List<Thread>list = new ArrayList<>();
            for(int j = i; j < i+3; j++) {
                List<String> strings = accounts.get(j);
                Thread thread = new Thread(new LoginAccounts(strings.get(0), strings.get(1),
                        strings.get(2), strings.get(3), strings.get(4)));
                thread.setName(strings.get(0));
                list.add(thread);
                if (list.size() == 3) {
                    threads.add(list);
                }
            }
        }

        for(int i = 0; i < threads.size(); i++){
            threads.get(i).forEach(Thread::start);
            while (true){
                if (threads.get(i).stream().filter(Thread::isInterrupted).count() == threads.get(i).size()) {
                    threads.remove(threads.get(i));
                    break;
                }
            }
        }
        System.out.println("Майнинг закончился!)");
    }

    public static List getAccountsInfo(){
        List list = new ArrayList();
        File file = new File(properties.getProperty("accounts"));
        try(Scanner scanner = new Scanner(new FileInputStream(file))){
            while (scanner.hasNextLine()){
                list.add(Arrays.stream(scanner.nextLine().split(" ")).collect(Collectors.toList()));
            }
        }catch (IOException exc ){
            exc.printStackTrace();
        }
        return list;
    }
}