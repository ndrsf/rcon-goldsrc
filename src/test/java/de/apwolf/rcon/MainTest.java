package de.apwolf.rcon;

import org.junit.Test;

public class MainTest {

	@Test
	public void testRun() {
		RconConnector connector = new RconConnector();
		String next = "a";
		for (int i = 0; i < 0x1f4b; i++) {
			System.out.println(next);
			next = connector.getNextPassword(next);
		}
	}

	@Test
	public void testGetNextPassword() {
		RconConnector connector = new RconConnector();
		System.out.println(connector.getNextPassword(" "));
	}

	@Test
	public void showAllowedChars() {
		RconConnector connector = new RconConnector();
		char c = 0x00;

		for (int i = 0; i < 0xff; i++) {
			if (connector.isValidRconPasswordChar(c)) {
				System.out.print(c);
			}
			c++;
		}
		System.out.println();
	}

}
