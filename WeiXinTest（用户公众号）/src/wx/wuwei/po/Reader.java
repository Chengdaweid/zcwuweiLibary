package wx.wuwei.po;
/**
 * ��Ӧ���ݿ���reader��
 * @author ZX50j
 *
 */
public class Reader {
	private String readerId;//���߱��
	private String name;//����
	private String sex;//�Ա�
	private String brithday;//����
	private String education;//ѧ�� 
	private String hobby ;//����
	private boolean flag; //�Ƿ����Э��
	public String getReaderId() {
		return readerId;
	}
	public void setReaderId(String readerId) {
		this.readerId = readerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getBrithday() {
		return brithday;
	}
	public void setBrithday(String brithday) {
		this.brithday = brithday;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getHobby() {
		return hobby;
	}
	public void setHobby(String hobby) {
		this.hobby = hobby;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	
}
