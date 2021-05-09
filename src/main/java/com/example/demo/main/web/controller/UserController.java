package com.example.demo.main.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.custom.AjaxResult;
import com.example.demo.custom.DataGridUtils;
import com.example.demo.custom.TablePageable;
import com.example.demo.main.entity.SysRole;
import com.example.demo.main.entity.User;
import com.example.demo.main.security.UserUtils;
import com.example.demo.main.service.SysRoleService;
import com.example.demo.main.service.UserService;
import com.example.demo.main.web.form.UserForm;

@Controller
@RequestMapping(value="/system/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private SysRoleService sysRoleService;
	
	@RequestMapping(value="/manage")
	public void manage() {
		
	}
	
	@RequestMapping(value="/page1")
	@ResponseBody
	public Object page1(TablePageable pageParam, UserForm form) {
		PageRequest pageable = pageParam.bulidPageRequest();
		Specification<User> spec = buildSpec(form);
		Page<User> page = userService.findAll(spec, pageable);
		return DataGridUtils.buildResult(page);
	}
	
	@RequestMapping(value="/save")
	@ResponseBody
	public Object save(UserForm form) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			User model = new User();
			//String name = userUtils.getName();
			Integer id = form.getId();
			if(id != null) {
				model = userService.findById(id);
				model.setUpdateDate(sdf.format(new Date()));
				//model.setUpdateName(name);
			}else {
				BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
				form.setPassword(encoder.encode("123456"));
				form.setCreateDate(sdf.format(new Date()));
				//form.setCreateName(name);
				model.setUpdateDate(sdf.format(new Date()));
				//model.setUpdateName(name);
			}
			BeanUtils.copyProperties(form, model,"id");
			
			List<SysRole> roles = new ArrayList<>();
			if(form.getRoles() != null && !form.getRoles().equals("")) {
				for (String code : form.getRoles().split(",")) {
					roles.add(sysRoleService.findbyCode(code));
				}				
			}
			model.setRoles(roles);
			userService.save(model);
			return new AjaxResult("数据保存成功");
		} catch (Exception e) {
			return new AjaxResult(false,"数据保存失败");
		}
	}
	
	@RequestMapping(value="/reset")
	@ResponseBody
	public Object reset(Integer id) {
		try {
			User user = userService.findById(id);
			BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
			user.setPassword(encoder.encode("123456"));
			userService.save(user);
			return new AjaxResult("重置成功");
		} catch (Exception e) {
			return new AjaxResult(false,"重置失败");
		}
	}
	
	@RequestMapping(value="/save1")
	@ResponseBody
	public Object save1(UserForm form) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			User model = new User();
			BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
			form.setPassword(encoder.encode(form.getPassword()));
			form.setCreateDate(sdf.format(new Date()));
			model.setUpdateDate(sdf.format(new Date()));
			BeanUtils.copyProperties(form, model,"id");
			
			List<SysRole> roles = new ArrayList<>();
			roles.add(sysRoleService.findbyCode("ROLE_USER"));
			model.setRoles(roles);
			userService.save(model);
			return new AjaxResult("数据保存成功");
		} catch (Exception e) {
			return new AjaxResult(false,"数据保存失败");
		}
	}

	private Specification<User> buildSpec(UserForm form) {
		return null;
	}
	
	@RequestMapping(value="/edit")        
	public void edit(UserForm form, ModelMap map) {
		User model = new User();
		Integer id = form.getId();
		if(id != null) {
			model = userService.findById(id);
		}
		map.put("model", model);
		List<SysRole> roles=sysRoleService.findByParentIsNull();
		map.put("roles", roles);
	}
	
	@RequestMapping(value="/delete")
	@ResponseBody
	public Object delete(UserForm form) {
		try {
			userService.deleteById(form.getId());
			return new AjaxResult("数据删除成功");
		} catch (Exception e) {
			return new AjaxResult(false,"数据删除失败");
		}
	}
}
