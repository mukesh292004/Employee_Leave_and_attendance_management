package com.example.demo.util;

import java.util.HashMap;
import java.util.Map;

public class LeaveTypes {
	public static final String CAUSAL = "casual";
	public static final String SICK = "sick";
	public static final String VACATION = "vacation";
	public static final String PERSONAL = "personal";

	public static Map<String, Integer> leaves() {
		Map<String, Integer> leaves = new HashMap<>();
		leaves.put(CAUSAL, 10);
		leaves.put(SICK, 8);
		leaves.put(VACATION, 15);
		leaves.put(PERSONAL, 5);
		return leaves;
	}
}
