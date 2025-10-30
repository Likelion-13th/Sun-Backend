package likelion13th.SunShop.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Properties s3Properties;
    private final AmazonS3 amazonS3;

    public String uploadFile(MultipartFile file){
        String bucketName = s3Properties.getBucket();
        String fileName = java.util.UUID.randomUUID()+"-"+ file.getOriginalFilename();

        try{
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);

            return amazonS3.getUrl(bucketName, fileName).toString();
        }catch (IOException e){
            throw new RuntimeException("S3 파일 업로드 실패", e);
        }


    }
}
