package org.springframework.roo.addon.json;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.addon.beaninfo.BeanInfoMetadata;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.itd.AbstractItdMetadataProvider;
import org.springframework.roo.classpath.itd.ItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.Path;

/**
 * Provides {@link JsonMetadata}.
 * 
 * @author Stefan Schmidt
 * @since 1.1
 */
@Component(immediate=true)
@Service 
public final class JsonMetadataProvider extends AbstractItdMetadataProvider {

	protected void activate(ComponentContext context) {
		metadataDependencyRegistry.registerDependency(PhysicalTypeIdentifier.getMetadataIdentiferType(), getProvidesType());
		addMetadataTrigger(new JavaType(RooJson.class.getName()));
		addMetadataTrigger(new JavaType("org.springframework.roo.addon.entity.RooIdentifier"));
	}

	protected void deactivate(ComponentContext context) {
		metadataDependencyRegistry.deregisterDependency(PhysicalTypeIdentifier.getMetadataIdentiferType(), getProvidesType());
		removeMetadataTrigger(new JavaType(RooJson.class.getName()));
		removeMetadataTrigger(new JavaType("org.springframework.roo.addon.entity.RooIdentifier"));
	}

	protected ItdTypeDetailsProvidingMetadataItem getMetadata(String metadataIdentificationString, JavaType aspectName, PhysicalTypeMetadata governorPhysicalTypeMetadata, String itdFilename) {
		// Acquire bean info (we need getters details, specifically)
		JavaType javaType = JsonMetadata.getJavaType(metadataIdentificationString);
		Path path = JsonMetadata.getPath(metadataIdentificationString);

		BeanInfoMetadata beanInfoMetadata = (BeanInfoMetadata) metadataService.get(BeanInfoMetadata.createIdentifier(javaType, path));

		// Abort if we don't have getter information available or if the target type is abstract
		if (beanInfoMetadata == null) {
			return null;
		}

		// We need to parse the annotation, if it is not present we will simply get the default annotation values
		JsonAnnotationValues annotationValues = new JsonAnnotationValues(governorPhysicalTypeMetadata);

		return new JsonMetadata(metadataIdentificationString, aspectName, governorPhysicalTypeMetadata, metadataService, annotationValues, beanInfoMetadata);
	}

	public String getItdUniquenessFilenameSuffix() {
		return "Json";
	}

	protected String getGovernorPhysicalTypeIdentifier(String metadataIdentificationString) {
		JavaType javaType = JsonMetadata.getJavaType(metadataIdentificationString);
		Path path = JsonMetadata.getPath(metadataIdentificationString);
		String physicalTypeIdentifier = PhysicalTypeIdentifier.createIdentifier(javaType, path);
		return physicalTypeIdentifier;
	}

	protected String createLocalIdentifier(JavaType javaType, Path path) {
		return JsonMetadata.createIdentifier(javaType, path);
	}

	public String getProvidesType() {
		return JsonMetadata.getMetadataIdentiferType();
	}
}