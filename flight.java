package com.qunar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

public class Flight {
	WebDriver driver;
	private static final String URL = "http://flight.qunar.com";
	private static final String WARNING = "每段航班需分别缴纳税费，请确认两航班均有效再付款";
	

	@Test
	public void bookFlight() throws InterruptedException {
		//启动浏览器，并输入查询条件
		driver.get(URL);
		driver.manage().window().maximize();
		Thread.sleep(500);
		WebElement searchTypeSng = driver.findElement(By.id("searchTypeSng"));
		if(!searchTypeSng.isSelected())
			searchTypeSng.click();
		WebElement fromCity = driver.findElement(By.name("fromCity"));
		fromCity.clear();
		fromCity.sendKeys("北京");
		Thread.sleep(500);
		WebElement toCity = driver.findElement(By.name("toCity"));
		toCity.clear();
		toCity.sendKeys("海口");
		WebElement fromDate = driver.findElement(By.name("fromDate"));
		fromDate.clear();
		fromDate.sendKeys(getDate());
		Thread.sleep(500);
		List<WebElement> buttonSet = driver.findElements(By.xpath("//button[contains(@class, ralignbtn)]"));
		buttonSet.get(0).click();
		Thread.sleep(2000);
		//跳转到机票单程搜索列表页
		//请参考http://blog.sina.com.cn/s/blog_e157953b0101hhxw.html
		//方式一
		WebDriverWait wait = new WebDriverWait(driver, 60);
		/*WebElement search_Done = wait.until(new ExpectedCondition<WebElement>() {
			@Override
			public WebElement apply(WebDriver driver) {
				WebElement element = driver.findElement(By.xpath("//span[@class='dec']"));
				return element;
			}
		});*/
		
		//Assert.assertTrue(search_Done.isDisplayed());
		//方式二
		wait.until(ExpectedConditions.textToBePresentInElement(By.xpath("//span[@class='dec']"),"搜索结束"));
		Assert.assertTrue(driver.findElement(By.xpath("//span[@class='dec']")).isDisplayed());
		
		Thread.sleep(2000);
		
		//判断航班列表是否出现
		List<WebElement> findList = driver.findElements(By.xpath("//div[@id='hdivResultPanel']/div"));
		//System.out.println(findList.size());
		if(findList.size()!= 0){
			List<WebElement> bookBtns = driver.findElements(By.xpath("//a[@title='点击查看订票网站']/span/b"));
			System.out.println(bookBtns.size());
			bookBtns.get(getIndex(bookBtns.size())).click();
			Thread.sleep(3000);
			if(isContentAppeared(driver, WARNING)){
				Assert.assertTrue(isContentAppeared(driver, "第一程"));
				Assert.assertTrue(isContentAppeared(driver, "第二程"));
			}else
				Assert.assertTrue(isContentAppeared(driver, "报价范围"));
			
			
		}else
			System.out.println("没有找到符合条件的航班");
	}

	@BeforeClass
	public void beforeClass() {
		driver = new FirefoxDriver();
	}

	@AfterClass
	public void afterClass() {
		driver.quit();
	}
	
	private static String getDate(){
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(calendar.DATE, 7);
		date = calendar.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		return dateString;
	}
	private static int getIndex(int num){
		Random random = new Random();
			int i = random.nextInt(num);
			return i;
	}
	
	private static boolean isContentAppeared(WebDriver driver, String str){
		boolean status = false;
		try{
			driver.findElement(By.xpath("//*[.,'"+str+"']"));
			status = true;
		}catch(NoSuchElementException e){
			status = false;
			System.out.println("'"+str+"'没有找到");
		}
		return status;
	}

}
