package com.syt.health.kitchen.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syt.health.kitchen.db.CourseModel;
import com.syt.health.kitchen.db.UserModel;
import com.syt.health.kitchen.db.common.NutrientModel;
import com.syt.health.kitchen.json.Course;
import com.syt.health.kitchen.json.CourseCondition;
import com.syt.health.kitchen.json.CourseCondition2;
import com.syt.health.kitchen.json.Food;
import com.syt.health.kitchen.json.GenerateCondition;
import com.syt.health.kitchen.json.LoginToken;
import com.syt.health.kitchen.json.Meal;
import com.syt.health.kitchen.json.Menu;
import com.syt.health.kitchen.json.Request;
import com.syt.health.kitchen.json.Return;
import com.syt.health.kitchen.json.ReturnHead;
import com.syt.health.kitchen.json.ReturnString;
import com.syt.health.kitchen.utils.Des3Util;
import com.syt.health.kitchen.utils.Utils;

public class RemoteServiceImpl implements IRemoteService {

	private static final String LOG_TAG = "ServiceImpl";
	public static int RETURN_CODE_SUCCESS = 0;
	public static String PRODUCT_NAME = "hc";

	private static IRemoteService service;

	private String base_url;
	private String tokenId = "";
	private String sid = "";
	private ObjectMapper mapper = new ObjectMapper();

	private RemoteServiceImpl(String url) {
		this.base_url = url;
	}

	public static IRemoteService getInstance() {
		if (service == null) {

			service = new RemoteServiceImpl("http://ws.sythealth.com/ws");
//			 service = new RemoteServiceImpl("http://192.168.0.110:8080/ws");
		}
		return service;
	}

	public boolean isLogin() {
		return tokenId != null && tokenId.length() > 0;
	}

	public <T> MessageModel<T> convert(Return<T> ret) throws ServiceException {
		if (ret.getHead().getRet() == RETURN_CODE_SUCCESS) {
			MessageModel<T> msg = new MessageModel<T>();
			msg.setFlag(true);
			msg.setMessage(ret.getHead().getMsg());
			msg.setData(ret.getData());

			return msg;
		} else {
			throw new ServiceException(ret.getHead().getRet(), ret.getHead()
					.getMsg());
		}
	}

	public <T> Return<T> convert(String org, TypeReference type,
			String url) throws ServiceException {
//		Log.d(LOG_TAG, org);
		try {
			Return<T> ret = null;
			try {
				ret = mapper.readValue(org, Return.class);
			} catch (JsonParseException e) {
				Log.e(LOG_TAG, e.getMessage());
			} catch (JsonMappingException e) {
				Log.e(LOG_TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage());
			}

			if (ret.getHead().getRet() == ServiceException.ERROR_CODE_UNAVAILABLE_TOKENID) {
				String oldToken = (tokenId == null)?null:new String(tokenId);

				loginDirect(sid);

				if(oldToken != null && oldToken.length() > 0){
				url = url.replace(oldToken, tokenId);
				}else{
					url = url.replace("tokenid=", "tokenid=" + tokenId);
				}

				ClientResource cr = new ClientResource(url);
				org = cr.get().getText();

				try {
					ret = mapper.readValue(org, Return.class);
				} catch (JsonParseException e) {
					Log.e(LOG_TAG, e.getMessage());
				} catch (JsonMappingException e) {
					Log.e(LOG_TAG, e.getMessage());
				} catch (IOException e) {
					Log.e(LOG_TAG, e.getMessage());
				}
			}else if (ret.getHead().getRet() == ServiceException.ERROR_CODE_NO_DATA_ERROR) {
				ret.getHead().setRet(RETURN_CODE_SUCCESS);
				ret.getHead().setMsg("抱歉!没有查到符合条件的数据");
			}

			String data = Des3Util.decode(ret.getEncryptdata());
//			Log.d(LOG_TAG, data);
			Object obj = null;
			try {
				obj = mapper.readValue(data, type);
			} catch (JsonParseException e) {
				Log.e(LOG_TAG, e.getMessage());
			} catch (JsonMappingException e) {
				Log.e(LOG_TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
			ret.setData((T) obj);

			return ret;
		} catch (RuntimeException re) {
			re.printStackTrace();
			Return<T> ret = new Return<T>();
			ReturnHead head = new ReturnHead();
			head.setRet(1);
			head.setMsg("convert error:" + re.getMessage());
			ret.setHead(head);
			ret.setData(null);
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			Return<T> ret = new Return<T>();
			ReturnHead head = new ReturnHead();
			head.setRet(1);
			head.setMsg("convert error:" + e.getMessage());
			ret.setHead(head);
			ret.setData(null);
			return ret;
		}
	}
	
	private void addClient2CR(ClientResource cr){
		
//		Context context = new Context();
//		context.getParameters().add("socketTimeout", "15000");
//		Client client = new Client(context, Protocol.HTTP);
//
//		cr.setNext(client);
	}

	public <T> Return<T> convert(String org, TypeReference type,
			ClientResource cr, String content) throws ServiceException {
//		Log.d(LOG_TAG, org);
		try {
			Return<T> ret = null;
			try {
				ret = mapper.readValue(org, Return.class);
			} catch (JsonParseException e) {
				Log.e(LOG_TAG, e.getMessage());
			} catch (JsonMappingException e) {
				Log.e(LOG_TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage());
			}

			if (ret.getHead().getRet() == ServiceException.ERROR_CODE_UNAVAILABLE_TOKENID) {
				String oldToken = (tokenId == null)?null:new String(tokenId);

				loginDirect(sid);

				if(oldToken != null){
					content = content.replace(oldToken, tokenId);
				}else{
					content = content.replace("tokenid=", "tokenid=" + tokenId);
				}
				
				Representation rep = new JsonRepresentation(content);

				org = cr.post(rep).getText();

				try {
					ret = mapper.readValue(org, Return.class);
				} catch (JsonParseException e) {
					Log.e(LOG_TAG, e.getMessage());
				} catch (JsonMappingException e) {
					Log.e(LOG_TAG, e.getMessage());
				} catch (IOException e) {
					Log.e(LOG_TAG, e.getMessage());
				}
			}else if (ret.getHead().getRet() == ServiceException.ERROR_CODE_NO_DATA_ERROR) {
				ret.getHead().setRet(RETURN_CODE_SUCCESS);
				ret.getHead().setMsg("抱歉!没有查到符合条件的数据");
			}

			String data = Des3Util.decode(ret.getEncryptdata());
//			Log.d(LOG_TAG, data);
			Object obj = null;
			try {
				obj = mapper.readValue(data, type);
			} catch (JsonParseException e) {
				Log.e(LOG_TAG, e.getMessage());
			} catch (JsonMappingException e) {
				Log.e(LOG_TAG, e.getMessage());
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
			ret.setData((T) obj);

			return ret;
		} catch (RuntimeException re) {
			re.printStackTrace();
			Return<T> ret = new Return<T>();
			ReturnHead head = new ReturnHead();
			head.setRet(1);
			head.setMsg("convert error:" + re.getMessage());
			ret.setHead(head);
			ret.setData(null);
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			Return<T> ret = new Return<T>();
			ReturnHead head = new ReturnHead();
			head.setRet(1);
			head.setMsg("convert error:" + e.getMessage());
			ret.setHead(head);
			ret.setData(null);
			return ret;
		}
	}

	@Override
	public UserModel loginDirect(String sid) throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/common/user/quicklogin?mobileid=" + sid
					+ "&desc=ANDROID" + "&product=" + PRODUCT_NAME);
			// Workaround for GAE servers to prevent chunk encoding
			// cr.setRequestEntityBuffering(true);
			// cr.accept();
			// cr.head(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<LoginToken>() {
			};

			String jsonStr = cr.get().getText();
			Return<LoginToken> ret = convert(jsonStr, type, base_url
					+ "/common/user/quicklogin?mobileid=" + sid
					+ "&desc=ANDROID" + "&product=" + PRODUCT_NAME);
			MessageModel<LoginToken> msg = convert(ret);
			if (!msg.isFlag()) {
				throw new ServiceException(
						ServiceException.ERROR_CODE_LOGIN_ERROR,
						msg.getMessage());
			} else {
				LoginToken login = msg.getData();
				tokenId = login.getTokenid();

				UserModel user = new UserModel();
				user.setSid(sid);
				// user.setForbidList(Arrays.asList(login.getForbidmodids()));

				this.sid = sid;

				return user;
			}

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (RuntimeException re) {
			re.printStackTrace();
			throw new ServiceException(re);
		}
	}

	@Override
	public UserModel login(String phone, String email, String password)
			throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/common/user/login?email=" + email + "&mobile=" + phone
					+ "&passwd=" + password + "&desc=ANDROID" + "product="
					+ PRODUCT_NAME);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			// cr.head(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<LoginToken>() {
			};

			String jsonStr = cr.get().getText();
			Return<LoginToken> ret = convert(jsonStr, type, base_url
					+ "/common/user/login?email=" + email + "&mobile=" + phone
					+ "&passwd=" + password + "&desc=ANDROID" + "product="
					+ PRODUCT_NAME);
			MessageModel<LoginToken> msg = convert(ret);
			if (!msg.isFlag()) {
				throw new ServiceException(
						ServiceException.ERROR_CODE_LOGIN_ERROR,
						msg.getMessage());
			} else {
			LoginToken login = msg.getData();
			tokenId = login.getTokenid();

			UserModel user = new UserModel();
			user.setForbidList(login.getForbidmodids());

			return user;
			}

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public MessageModel<String> forgetPassword(String phone, String email)
			throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/common/user/login?email=" + email + "&mobile=" + phone
					+ "&desc=ANDROID" + "product=" + PRODUCT_NAME);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			// cr.head(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			String jsonStr = cr.get().getText();
			Return<String> ret = convert(jsonStr, new TypeReference<Return>(){}, base_url
					+ "/common/user/login?email=" + email + "&mobile=" + phone
					+ "&desc=ANDROID" + "product=" + PRODUCT_NAME);
			return convert(ret);

		} catch (ResourceException e1) {
			throw new ServiceException(e1);
		} catch (IOException e1) {
			throw new ServiceException(e1);
		}
	}

	@Override
	public MessageModel<String> register(UserModel user)
			throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/common/user/register?email=" + user.getEmail()
					+ "&mobile=" + user.getPhoneNumber() + "&passwd="
					+ user.getPassword() + "&mobileid=" + user.getSid());
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			// cr.head(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			String jsonStr = cr.get().getText();
			Return<String> ret = convert(jsonStr, new TypeReference<Return>(){},
					base_url + "/common/user/register?email=" + user.getEmail()
							+ "&mobile=" + user.getPhoneNumber() + "&passwd="
							+ user.getPassword() + "&mobileid=" + user.getSid());
			return convert(ret);

		} catch (ResourceException e1) {
			throw new ServiceException(e1);
		} catch (IOException e1) {
			throw new ServiceException(e1);
		}
	}

	@Override
	public Menu fetchMealsByDate(String date) throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/hc/menu/bydate?tokenid=" + tokenId + "&menudate="
					+ date);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<Menu>() {
			};

			String jsonStr = cr.get().getText();
			Return<Menu> ret = convert(jsonStr, type, base_url
					+ "/hc/menu/bydate?tokenid=" + tokenId + "&menudate="
					+ date);
			MessageModel<Menu> msg = convert(ret);
			
			return msg.getData();

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public Menu generateMenuByDate(String date, GenerateCondition params)
			throws ServiceException {

		params.setMenudate(date);

			ClientResource cr = new ClientResource(base_url
					+ "/hc/menu/generate");
			try {
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<Request<GenerateCondition>>() {
			};

			Request<GenerateCondition> request = new Request<GenerateCondition>();
			request.setTokenid(tokenId);
			request.setData(params);

//			Log.d(LOG_TAG, request.toString());

			String content = null;
			try {
				content = mapper.writeValueAsString(request);
			} catch (JsonProcessingException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
			Representation rep = new JsonRepresentation(content);
			rep.setMediaType(MediaType.APPLICATION_JSON);

			String jsonStr = cr.post(rep).getText();
			rep.release();
			type = new TypeReference<Menu>() {
			};
			Return<Menu> ret = convert(jsonStr, type, cr, content);
			MessageModel<Menu> msg = convert(ret);
			Menu menu = msg.getData();
			menu.setSmartParams(params);

			return menu;

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}finally {
		    cr.release();
		    if(cr.getResponseEntity() != null){
		    cr.getResponseEntity().release();
		    }
		}
	}

	@Override
	public void setBaseUrl(String url) {
		this.base_url = url;

	}

	@Override
	public String getBaseUrl() {
		return this.base_url;
	}

	@Override
	public List<Course> fetchCourseByParams(CourseCondition params)
			throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/resource/course/bycondition");
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<Request<CourseCondition>>() {
			};

			Request<CourseCondition2> request = new Request<CourseCondition2>();
			request.setTokenid(tokenId);
			request.setData(CourseCondition2.copyCondition(params));

			String content = null;
			try {
				content = mapper.writeValueAsString(request);
			} catch (JsonProcessingException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
			Representation rep = new JsonRepresentation(content);
			rep.setMediaType(MediaType.APPLICATION_JSON);

			String jsonStr = cr.post(rep).getText();
			type = new TypeReference<List<Course>>() {
			};
			Return<List<Course>> ret = convert(jsonStr, type, cr, content);
			MessageModel<List<Course>> msg = convert(ret);
			return msg.getData();
		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public Course queryCourseById(String id) throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/resource/course/byid?tokenid=" + tokenId + "&courseid="
					+ id);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<Course>() {
			};

			String jsonStr = cr.get().getText();
			Return<Course> ret = convert(jsonStr, type, base_url
					+ "/resource/course/byid?tokenid=" + tokenId + "&courseid="
					+ id);
			MessageModel<Course> msg = convert(ret);
			return msg.getData();

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public List<Course> queryCourseByKeyword(String key, boolean filtercond,
			int page) throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/resource/course/bykeyname?tokenid=" + tokenId
					+ "&keyname=" + key + "&page=" + page + "&filtercond="
					+ (filtercond ? "yes" : "no"));
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<List<Course>>() {
			};

			String jsonStr = cr.get().getText();
			Return<List<Course>> ret = convert(jsonStr, type, base_url
					+ "/resource/course/bykeyname?tokenid=" + tokenId
					+ "&keyname=" + key + "&page=" + page + "&filtercond="
					+ (filtercond ? "yes" : "no"));
			MessageModel<List<Course>> msg = convert(ret);
			return msg.getData();

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public List<Food> queryFoodByKeyword(String key, int page)
			throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/resource/food/bykeyname?tokenid=" + tokenId
					+ "&keyname=" + key + "&page=" + page);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<List<Food>>() {
			};

			String jsonStr = cr.get().getText();
			Return<List<Food>> ret = convert(jsonStr, type, base_url
					+ "/resource/food/bykeyname?tokenid=" + tokenId
					+ "&keyname=" + key + "&page=" + page);
			MessageModel<List<Food>> msg = convert(ret);
			return msg.getData();

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}


	@Override
	public Menu createMenuByDate(String date) throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url + "/hc/menu/create");
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<Request<Menu>>() {
			};

			Request<Menu> request = new Request<Menu>();
			request.setTokenid(tokenId);
			Menu menu = new Menu();
			menu.setMenudate(date);
			fillMenu(menu);
			request.setData(menu);

			String content = null;
			try {
				content = mapper.writeValueAsString(request);
			} catch (JsonProcessingException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
			Representation rep = new JsonRepresentation(content);
			rep.setMediaType(MediaType.APPLICATION_JSON);

			String jsonStr = cr.post(rep).getText();

			type = new TypeReference<Menu>() {
			};
			Return<Menu> ret = convert(jsonStr, type, cr, content);
			MessageModel<Menu> msg = convert(ret);
			return msg.getData();

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	private void fillMenu(Menu menu) {
		Meal meal = new Meal();
		menu.setBreakfast(meal);
		meal = new Meal();
		menu.setFruit(meal);
		meal = new Meal();
		menu.setLunch(meal);
		meal = new Meal();
		menu.setDinner(meal);
	}

	@Override
	public Meal generateMealByHealthCondition(Meal meal,
			GenerateCondition params) throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/hc/meal/generate");
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<Request<GenerateCondition>>() {
			};

			Request<GenerateCondition> request = new Request<GenerateCondition>();
			request.setTokenid(tokenId);
			params.setMealid(meal.getId());
			params.setMenuid(meal.getMenuid());
			request.setData(params);

			String content = null;
			try {
				content = mapper.writeValueAsString(request);
			} catch (JsonProcessingException e) {
				Log.e(LOG_TAG, e.getMessage());
			}
			Representation rep = new JsonRepresentation(content);
			rep.setMediaType(MediaType.APPLICATION_JSON);

			String jsonStr = cr.post(rep).getText();
			type = new TypeReference<Meal>() {
			};
			Return<Meal> ret = convert(jsonStr, type, cr, content);
			MessageModel<Meal> msg = convert(ret);
			return msg.getData();

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public Food queryFoodById(String id) throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/resource/food/byid?tokenid=" + tokenId + "&foodid=" + id);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<Food>() {
			};

			String jsonStr = cr.get().getText();
			Return<Food> ret = convert(jsonStr, type, base_url
					+ "/resource/food/byid?tokenid=" + tokenId + "&foodid=" + id);
			MessageModel<Food> msg = convert(ret);
			return msg.getData();

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public void addCourseToMeal(String mealId, String courseId,
			double quantity) throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/hc/meal/addcourse?tokenid="
					+ tokenId
					+ "&mealid="
					+ mealId
					+ "&courseid="
					+ courseId
					+ (quantity < 0 ? "" : "&quantity="
							+ String.format("%.2f", quantity)));
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			String jsonStr = cr.get().getText();
			Return ret = convert(
					jsonStr,
					new TypeReference<String>(){},
					base_url
							+ "/hc/meal/addcourse?tokenid="
							+ tokenId
							+ "&mealid="
							+ mealId
							+ "&courseid="
							+ courseId
							+ (quantity < 0 ? "" : "&quantity="
									+ String.format("%.2f", quantity)));

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public void modifyCourseToMeal(String mealId, String courseId,
			double quantity) throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/hc/meal/updatequantity?tokenid="
					+ tokenId
					+ "&mealid="
					+ mealId
					+ "&courseid="
					+ courseId
					+ (quantity < 0 ? "" : "&quantity="
							+ String.format("%.2f", quantity)));
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			String jsonStr = cr.get().getText();
			Return<Course> ret = convert(
					jsonStr,
					new TypeReference<String>(){},
					base_url
							+ "/hc/meal/updatequantity?tokenid="
							+ tokenId
							+ "&mealid="
							+ mealId
							+ "&courseid="
							+ courseId
							+ (quantity < 0 ? "" : "&quantity="
									+ String.format("%.2f", quantity)));

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public void removeCourseFromMeal(String mealId, String courseId)
			throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/hc/meal/delcourse?tokenid=" + tokenId + "&mealid="
					+ mealId + "&courseid=" + courseId);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<Meal>() {
			};

			String jsonStr = cr.get().getText();
			Return ret = convert(jsonStr, type, base_url
					+ "/hc/meal/delcourse?tokenid=" + tokenId + "&mealid="
					+ mealId + "&courseid=" + courseId);

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public String getMenuCommentsByDate(String date) throws ServiceException {
		try {
			ClientResource cr = new ClientResource(base_url
					+ "/hc/menu/bysummary?tokenid=" + tokenId + "&menudate="
					+ date);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<ReturnString>() {
			};

			String jsonStr = cr.get().getText();
			Return<ReturnString> ret = convert(jsonStr, type, base_url
					+ "/hc/menu/bysummary?tokenid=" + tokenId + "&menudate="
					+ date);
			MessageModel<String> msg = convert2(ret);
			return msg.getData();

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}

	}

	@Override
	public List<NutrientModel> getMenuNutrientsByDate(String date,
			List<NutrientModel> list) throws ServiceException {
		/*
		 * List<NutrientModel> list2 = new ArrayList<NutrientModel>(); for
		 * (NutrientModel nm : list) { NutrientModel nm2 = new NutrientModel();
		 * NutrientModel.copy(nm2, nm); nm2.setContent(new
		 * Random().nextInt(2000)); list2.add(nm2); } return list2;
		 */
		
			ClientResource cr = new ClientResource(base_url
					+ "/hc/menu/getnutrients?tokenid=" + tokenId + "&menudate="
					+ date); //
			try {
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			TypeReference type = new TypeReference<List<NutrientModel>>() {
			};

			String jsonStr = cr.get().getText();
			Return<List<NutrientModel>> ret = convert(jsonStr, type, base_url
					+ "/hc/menu/getnutrients?tokenid=" + tokenId + "&menudate="
					+ date);

			if (ret.getHead().getRet() == RETURN_CODE_SUCCESS) {
				List<NutrientModel> rlist = ret.getData();
				List<NutrientModel> list2 = new ArrayList<NutrientModel>();
				if(rlist != null){
				for (NutrientModel nm : list) {
					int pos = rlist.indexOf(nm);

					if (pos >= 0) {
						NutrientModel nm2 = new NutrientModel();
						NutrientModel.copy(nm2, nm);
						nm2.setContent(rlist.get(pos).getContent());
						list2.add(nm2);
					} else if (nm.getName().contains(Utils.RE_LIANG)) {
						NutrientModel nm2 = new NutrientModel();
						NutrientModel.copy(nm2, nm);
						list2.add(nm2);
//						Log.d(LOG_TAG, "Cals inserting...");
					}
				}
				}
				return list2;
			} else {
				throw new ServiceException(ret.getHead().getRet(), ret
						.getHead().getMsg());
			}

		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}finally {
		    cr.release();
		    if(cr.getResponseEntity() != null){
		    cr.getResponseEntity().release();
		    }
		}

	}

	private MessageModel<String> convert2(Return<ReturnString> ret) {
		MessageModel<String> msg = new MessageModel<String>();
		msg.setFlag(ret.getHead().getRet() == RETURN_CODE_SUCCESS);
		msg.setMessage(ret.getHead().getMsg());
		msg.setData(ret.getData().getString());
		return msg;
	}

	@Override
	public List<Course> fetchCourseByCals(int value, boolean ascendOrder,
			int page) throws ServiceException {
		try {
			String url = base_url
					+ "/resource/course/bycalories?tokenid=" + tokenId + "&value="
					+ value +"&orderby=" + (ascendOrder?"asc":"desc") + "&page=" + page;
			ClientResource cr = new ClientResource(url);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			String jsonStr = cr.get().getText();
			TypeReference type = new TypeReference<List<Course>>() {
			};
			Return<List<Course>> ret = convert(jsonStr, type, cr, url);
			MessageModel<List<Course>> msg = convert(ret);
			return msg.getData();
		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public List<Course> fetchCourseByFood(String name, List<String> health, int page)
			throws ServiceException {
		try {
			String url = base_url
					+ "/resource/course/byfood?tokenid=" + tokenId + "&name="
					+ name + "&health="
							+ ((health != null && health.size() > 0)?Utils.arrayIntoString(health):"") 
							+ "&page=" + page;
			ClientResource cr = new ClientResource(url);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			String jsonStr = cr.get().getText();
			TypeReference type = new TypeReference<List<Course>>() {
			};
			Return<List<Course>> ret = convert(jsonStr, type, cr, url);
			MessageModel<List<Course>> msg = convert(ret);
			return msg.getData();
		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public List<Food> fetchFoodByHealth(String name, String filter, int page)
			throws ServiceException {
		try {
			String url = base_url
					+ "/resource/food/byhealth?tokenid=" + tokenId + "&name="
					+ name + "&filter=" + filter + "&page=" + page;
			ClientResource cr = new ClientResource(url);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			String jsonStr = cr.get().getText();
			TypeReference type = new TypeReference<List<Food>>() {
			};
			Return<List<Food>> ret = convert(jsonStr, type, cr, url);
			MessageModel<List<Food>> msg = convert(ret);
			return msg.getData();
		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public List<Food> fetchFoodByNutrient(String name, String filter, int page)
			throws ServiceException {
		try {
			String url = base_url
					+ "/resource/food/bynutrient?tokenid=" + tokenId + "&name="
					+ name + "&filter=" + filter + "&page=" + page;
			ClientResource cr = new ClientResource(url);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			String jsonStr = cr.get().getText();
			TypeReference type = new TypeReference<List<Food>>() {
			};
			Return<List<Food>> ret = convert(jsonStr, type, cr, url);
			MessageModel<List<Food>> msg = convert(ret);
			return msg.getData();
		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public List<Food> fetchFoodByCals(int value, boolean ascendOrder, int page)
			throws ServiceException {
		try {
			String url = base_url
					+ "/resource/food/bycalories?tokenid=" + tokenId + "&value="
					+ value +"&orderby=" + (ascendOrder?"asc":"desc") + "&page=" + page;
			ClientResource cr = new ClientResource(url);
			// Workaround for GAE servers to prevent chunk encoding
			cr.setRequestEntityBuffering(true);
			// cr.accept(MediaType.APPLICATION_JSON);
			addClient2CR(cr);

			String jsonStr = cr.get().getText();
			TypeReference type = new TypeReference<List<Food>>() {
			};
			Return<List<Food>> ret = convert(jsonStr, type, cr, url);
			MessageModel<List<Food>> msg = convert(ret);
			return msg.getData();
		} catch (ResourceException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ServiceException(e1);
		}
	}

	@Override
	public void setSid(String sid) {
		this.sid = sid;
	}

}
