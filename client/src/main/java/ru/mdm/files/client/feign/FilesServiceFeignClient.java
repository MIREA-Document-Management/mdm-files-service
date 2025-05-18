package ru.mdm.files.client.feign;

import reactivefeign.spring.config.ReactiveFeignClient;
import ru.mdm.files.api.FileRestApi;
import ru.mdm.files.client.configuration.feign.FeignClientConfiguration;

/**
 * Клиент для работы с файлами.
 */
@ReactiveFeignClient(name = "mdm-files-client", configuration = FeignClientConfiguration.class,
        url = "${mdm.files.service.url}", path = FileRestApi.BASE_PATH)
public interface FilesServiceFeignClient extends FileRestApi {
}
