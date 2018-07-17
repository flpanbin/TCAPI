package com.tc.controller;

import com.jfinal.core.Controller;

public class HelloController extends Controller {

	public void index() {
		renderText("this is helloworld!");
	}
}
