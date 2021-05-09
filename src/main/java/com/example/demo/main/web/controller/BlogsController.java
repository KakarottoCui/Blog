package com.example.demo.main.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.custom.AjaxResult;
import com.example.demo.custom.CommonController;
import com.example.demo.custom.DataGridParam;
import com.example.demo.custom.DataGridUtils;
import com.example.demo.custom.FlowloadUtils;
import com.example.demo.main.entity.Blogs;
import com.example.demo.main.entity.Comment;
import com.example.demo.main.entity.SysUser;
import com.example.demo.main.security.UserUtils;
import com.example.demo.main.service.BlogsSerive;
import com.example.demo.main.service.CommentService;
import com.example.demo.main.service.SystemDataService;
import com.example.demo.main.web.form.BlogsForm;

@Controller
@RequestMapping(value="/blog")
public class BlogsController extends CommonController<Blogs, Integer, BlogsForm> {
	@Autowired
	private BlogsSerive blogService;
	@Autowired
	private SystemDataService dataService;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private CommentService commentService;
	
	@Override
	public void manage(ModelMap map) {
		map.put("user", userUtils.getUser());
		super.manage(map);
	}

	@Override
	public void edit(BlogsForm form, ModelMap map) throws InstantiationException, IllegalAccessException {
		map.put("sf", dataService.findByDictionariesCode("SJZD_BKFL"));
		map.put("type", dataService.findByDictionariesCode("SJZD_WZLX"));
		super.edit(form, map);
	}

	@Override
	public Object save(BlogsForm form) {
		System.out.println(form.getText()+"=========");
		form.setUser(userUtils.getUser());
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Blogs model = new Blogs();
			String name = userUtils.getName();
			Integer id = form.getId();
			if(id != null) {
				model = blogService.findById(id);
				model.setUpdateDate(sdf.format(new Date()));
				model.setUpdateName(name);
			}else {
				form.setCreateDate(sdf.format(new Date()));
				form.setCreateName(name);
				model.setUpdateDate(sdf.format(new Date()));
				model.setUpdateName(name);
			}
			BeanUtils.copyProperties(form, model,"id");
			blogService.save(model);
			Integer id2;
			if(form.getId() != null) {
				id2 = form.getId();
			}else {
				id2 = blogService.findByUserNicknameAndUpdateDate(name, model.getUpdateDate()).getId();
			}
			return new AjaxResult(id2+"");
		} catch (Exception e) {
			return new AjaxResult(false,"数据保存失败");
		}
	}
	
	/**
	 * 访问页面
	 * @param id
	 * @param map
	 */
	@RequestMapping(value="/visit")
	public void visit(Integer id, ModelMap map) {
		map.put("blog", blogService.findById(id));
		map.put("comment", commentService.findByBlogId(id));
	}

	/**
	 * 访问页面数据
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/blogText")
	@ResponseBody
	public Object blogText(Integer id) {
		return blogService.findById(id).getText();
	}
	
	@RequestMapping(value="/audit")
	public void audit(ModelMap map) {
		
	}
	
	@RequestMapping(value="/indexBlog")
	public void indexBlog() {
		
	}
	
	/**
	 * 同意
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/consent")
	@ResponseBody
	public Object consent(Integer id) {
		try {
			Blogs blogs = blogService.findById(id);
			blogs.setBlogStatic("审核通过");
			blogService.save(blogs);
			return new AjaxResult("同意成功");
		} catch (Exception e) {
			return new AjaxResult(false,"同意失败");
		}
	}
	
	/**
	 * 拒绝
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/refuse")
	@ResponseBody
	public Object refuse(Integer id, String jy) {
		try {
			Blogs blogs = blogService.findById(id);
			blogs.setBlogStatic("已拒绝");
			blogs.setSuggest(jy);
			blogService.save(blogs);
			return new AjaxResult("拒绝成功");
		} catch (Exception e) {
			return new AjaxResult(false,"拒绝失败");
		}
	}
	
	@RequestMapping(value="/fysj")
	@ResponseBody
	public Object fysj(Integer page, BlogsForm form) {
		Sort sort=Sort.by(Sort.Direction.DESC, "updateDate");
		Pageable pageable = PageRequest.of(page-1, 10, sort);
		Page<Blogs> blog;
		Specification<Blogs> spec = buildSpec2(form);
		blog = blogService.findAll(spec, pageable);
		return FlowloadUtils.buildResult(blog);
	}
	
	@RequestMapping(value="/fysj2")
	@ResponseBody
	public Object fysj2(Integer page, BlogsForm form) {
		Sort sort=Sort.by(Sort.Direction.DESC, "updateDate");
		Pageable pageable = PageRequest.of(page-1, 10, sort);
		Page<Blogs> blog;
		Specification<Blogs> spec = buildSpec3(form);
		blog = blogService.findAll(spec, pageable);
		return FlowloadUtils.buildResult(blog);
	}
	
	private Specification<Blogs> buildSpec3(BlogsForm form) {
		Specification<Blogs> specification = new Specification<Blogs>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public Predicate toPredicate(Root<Blogs> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				HashSet<Predicate> rules=new HashSet<>();
				Predicate difficultylevel = cb.equal(root.get("blogStatic"), "审核通过");
				rules.add(difficultylevel);
				return cb.and(rules.toArray(new Predicate[rules.size()]));
			}

		};
		return specification;
	}

	private Specification<Blogs> buildSpec2(BlogsForm form) {
		Specification<Blogs> specification = new Specification<Blogs>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public Predicate toPredicate(Root<Blogs> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				HashSet<Predicate> rules=new HashSet<>();
				SysUser user = userUtils.getUser();
				Predicate difficultylevel = cb.equal(root.get("user").get("id"), user.getId());
				rules.add(difficultylevel);
				return cb.and(rules.toArray(new Predicate[rules.size()]));
			}

		};
		return specification;
	}

	@RequestMapping(value="/page2")
	@ResponseBody
	public HashMap<String, Object> page2(ModelMap map, DataGridParam param, BlogsForm form) {
		Sort sort=Sort.by("id");
		Pageable pabeable = param.getPageable(sort);
		Specification<Blogs> spec = buildSpec1(form);
		Page<Blogs> page = blogService.findAll(spec, pabeable);
		return DataGridUtils.buildResult(page);
	}

	private Specification<Blogs> buildSpec1(BlogsForm form) {
		Specification<Blogs> specification = new Specification<Blogs>() {

			private static final long serialVersionUID = 1L;
			
			@Override
			public Predicate toPredicate(Root<Blogs> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				HashSet<Predicate> rules=new HashSet<>();
				Predicate difficultylevel = cb.like(root.get("blogStatic"), "%待审核%");
				rules.add(difficultylevel);
				return cb.and(rules.toArray(new Predicate[rules.size()]));
			}

		};
		return specification;
	}
	
	/**
	 * 评论
	 */
	@RequestMapping(value="/comment")
	@ResponseBody
	public Object comment(String comment, Integer id) {
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Comment model = new Comment();
			model.setText(comment);
			model.setUser(userUtils.getUser());
			model.setBlog(blogService.findById(id));
			model.setRead2(false);
			model.setCreateDate(sdf.format(new Date()));
			commentService.save(model);
			return new AjaxResult("评论成功");
		} catch (Exception e) {
			return new AjaxResult(false,"评论失败");
		}
	}
}
