package com.dershaneproject.randevu;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
/*import org.springframework.context.ConfigurableApplicationContext;
import com.dershaneproject.randevu.dataAccess.abstracts.DayOfWeekDao;
import com.dershaneproject.randevu.dataAccess.abstracts.HourDao;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemStaffDao;
import com.dershaneproject.randevu.dataAccess.abstracts.SystemWorkerDao;
import com.dershaneproject.randevu.entities.concretes.DayOfWeek;
import com.dershaneproject.randevu.entities.concretes.Hour;
import com.dershaneproject.randevu.entities.concretes.SystemStaff;
import com.dershaneproject.randevu.entities.concretes.SystemWorker;
import java.time.LocalTime;
import java.util.Optional;*/
import org.springframework.context.annotation.Bean;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.annotations.OpenAPI31;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@EnableAutoConfiguration
@OpenAPI31
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class RandevuApplication {

	/*
	 * @Author ( Dad ) : Emin Palaz
	 * 
	 * Notlar:
	 * 
	 * ÖNEMLİ - Swaggerdan ötürü güvenlik açığı olmaması çin en son ayarlarını
	 * düzenle veya sil - Tercihen düzenle
	 * 
	 * Data transfer objectler sitem çalıştıktan sonra eklenicek ki zaman
	 * kaybedilmesin. Sistem önce çalışıcak istenildiği şekilde sonrasında ise Dto
	 * şifrelerin hashlanmesi vs. devreye sokulacaktır. Özetle Performans Güvenlik
	 * vs En son yapılacak Hash alg = SHA-1 ( Araştırılacak ) Şifreleme = RSA (
	 * Araştırılacak )
	 * 
	 * 
	 * 
	 * - 5 Ocak 2023 not: Sistemin en temel nesneleri yazıldı aralarındaki ilişkiler
	 * kontrol edilmeden diğerleri yazılmayacak. Mantıkta bir problem yoksa
	 * Nesnelerin türevleri eklenip tekrar denen icek. Bugün testleri yapılmadı hata
	 * olma olasılığı var dikkatli ol.
	 * 
	 * -24 ocak 2023 not: şuanda TeacherControllerda getall isteğimi
	 * gerçekleşitirdim ve userdaki sütünlarımıda sorunsuz bir şekilde çekiyor ve id
	 * ye göre birinide getiriyor extend edilmiş classımıda getirdi otomatik olarak
	 * yarın diğer metotlar denenecek ve bir sorunla karşılaşılmaz ise de yeni
	 * classlar eklenecek (Department ı bitir).
	 * 
	 * -1 Şubat 2023 not: Projenin alt yapısı genel olarak oluşturuldu bundan sonra
	 * altyapısını düzgün kafayla gözden geçir. sonra iş kurallarını yaz. Dto lar
	 * yazıldı ama bağlantılarda sorun olabilir.
	 * 
	 * -2 Şubat 2023: şuanda son tabloların managerları yazılıyor haftasonu bitene
	 * kadar çalışır hale getir.
	 * 
	 * Kendime not: Başka nesnelerle ilişkisi olan Nesne ni managerı yazarken diğer
	 * ilişkiler ile alakalı bir güncelleme silme gibi vs. dataAccess katmanını
	 * kullan ama değişiklik falan yapılacak o değişiklik yapılacak classın managerı
	 * üzerinden işlemlerini yap.
	 * 
	 * 
	 * 
	 * 
	 *   //// BUNDAN SONRASI ÇOK ÖNEMLİ ////
	 * 
	 * 4 Şubat 2023: An itibariyle bir öğretmen oluşturulduğunda otomatik olarak ders saatleride 
	 * oluşturuluyor ve güncellenebiliyor temel randevu olayında bir sorun yok şu anda ama saatleri ilişkisel olarak
	 * çektiğimizde sadece full olup olmadığı ve yaratıla güncellenme tarihleri geliyor bu yüzden bunlar ile ilgili
	 * detaylı bilgiler için üstünde tıklandığında onun id si üzerinden tekrar çağır ve veriler getir
	 * Mesela en son güncelleyen sistem çalışanı, hangi gün veya hangi saat gibi bilgiler için 
	 * ilk geldiğinde id si üzerinde tabloda iki boyutlo array ile yerleştir ( Sırayla geliyor id leri bunu kullan )
	 * 
	 * 
	 * 
	 * !!! not: Şuanda haftanın 7 günü sabah 8 akşam 10 a kadar ayarlandı !!! Önemli
	 * 
	 * Sistemde sadece tek öğretmeni getirdiğimizde randevuları getiriyor sistemde performans sorunu yaratır aksi
	 * takdirde diğer türlü randevuler direkt kendi controllerı ile ulaşabiliriz. Arayüzde javascriptte modallar ile
	 * penceler açılıp oradan bireysel ve detaylı schedule bilgilerine ulaşılabilecek.
	 * 
	 * 
	 * Önemli özellikler eklenicek girilen değerlerin sınırlandırılması mesela parola da sınırlamalar olucak null
	 * verilemeyecek gibi şeyler bunlaru bitir taslaktan sonra.
	 * 
	 * 
	 * 5 Şubat 2023: Bugün tüm servisler test edilip başarılı olduklarına dair not edilecek.
	 * 
	 *  
	 * 11 Mart 2023: Sistem mantığı komple değiştirildi şuanda Teacherda schedullar düzgün verilince herhangi bir sorun olmuyor
	 * ama onlar kaydedilmesede teacherı kaydediyor onlara teacher id yi vermek için önce teacherı kaydetmiştim bu sorun 
	 * çözülecek.(Önce schedullerı gönderdim burada bi hata oluşmaz ise teacher kaydedilip o schedulların teacherId leri 
	 * güncellenebilir buna bak) Birde modelMapperlara bakılacak oradada sorunlar var.
	 * 
	 * 
	 * 
	 * Bu projenin taslağı çalışır hale geldiğinde öğrenmek için unit testlerini
	 * yaz. ( Acelesi yok amaç tecrübe etmek ondan düzgün ve sakin ve de emin bir
	 * şekilde ilerle )
	 * 
	 */

	
	public static void main(String[] args) {

		// Uygulama çalışınca oluşan classları daha doğrusu
		// beanleri kullanmak için instence oluşturdum

	//	ConfigurableApplicationContext configurableApplicationContext = 
		SpringApplication.run(RandevuApplication.class, args);
		
		/* Versiyon kontrolleri */
		System.out.println(" My jdk version " + System.getProperty("java.version"));
		System.out.println(" Hibernate version " + org.hibernate.Version.getVersionString());

	/*	 SystemWorker deneme = new SystemWorker();
		 deneme.setUserName("auth kontrolü");
		 deneme.setAuthority(1);
		 
	     SystemWorkerDao dao = configurableApplicationContext.getBean(SystemWorkerDao.class);
		 Optional<SystemWorker> deneme1 =  dao.findById((long) 1);
		 deneme1.get().setEmail("abc");
		 dao.save(deneme1.get());
    */
		// Diğer Userları tanımla. Repo Testi için

	}
	
	@Bean
	public OpenAPI customOpenAPI(@Value("not: "
			+ "'Authorization' "
			+ "parametresine ne verirseniz verin swaggerda tanımladığımız tokenı gönderiyor.")
	        String description,
			@Value("v1") String version) {
	    final String securitySchemeName = "bearerAuth";						
		return new OpenAPI()
	            .components(
	                    new Components()
	                            .addSecuritySchemes(securitySchemeName,
	                                    new SecurityScheme()
	                                            .type(SecurityScheme.Type.HTTP)
	                                            .scheme("bearer")
	                                            .bearerFormat("JWT")
	                            )
	            )
	            .security(List.of(new SecurityRequirement().addList(securitySchemeName))).info(new Info()
				.title("Dershane Randevu Api")
				.version(version)
				.description(description)
				.license(new License().name("Randevu Api Licence")));	
	}
    
    @Bean
    public ModelMapper getModelMapper() {
    	ModelMapper modelMapper = new ModelMapper();
    	return modelMapper;
    	
    	
    }

}
