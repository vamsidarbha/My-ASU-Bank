package bankapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;

import com.bankapp.MyAsuBankApplication;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MyAsuBankApplication.class)
@WebAppConfiguration
public class MyAsuBankApplicationTests {

	@Test
	public void contextLoads() {
	}

}
