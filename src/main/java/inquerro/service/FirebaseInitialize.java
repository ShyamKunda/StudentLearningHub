package inquerro.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.util.concurrent.ExecutionError;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FirebaseInitialize {


    Logger logger = LoggerFactory.getLogger(FirebaseInitialize.class);

    @PostConstruct //Initialise this method during startUp
    //todo 1. Add Proper logger
    //todo 2. Add profile for dev and prod environment
    //todo 3. Add proper exception
   public void initialize() {

        FileSearch fileSearch = new FileSearch();
        try {

            String fileName2 =fileSearch.searchDirectory(new File("/tmp/eb_extracted_jar/BOOT-INF/classes"), "serviceAccount.json");

            FileInputStream serviceAccount =
                    new FileInputStream(fileName2);
            //InputStream resource = this.getClass().getClassLoader().getResourceAsStream("/data/serviceAccount.json");
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://inquerro-8ee6e.firebaseio.com")
                    .build();

            if (FirebaseApp.getApps() != null) {
                FirebaseApp.initializeApp(options);
            }
        }
        catch (Exception e){
            logger.error(e.getMessage());
        }
   }
}
