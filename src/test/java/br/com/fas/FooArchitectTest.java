package br.com.fas;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import org.junit.Test;

import br.com.fas.persistence.Dao;

public class FooArchitectTest {
    

    JavaClasses importedClasses = new ClassFileImporter()
    .withImportOption(new com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests())
    .importPackages("br.com.fas");

    @Test
    public void verificarDependenciasParaCamadaPersistencia() {

        ArchRule rule = classes()
        .that().resideInAPackage("..persistence..")
        .should().onlyHaveDependentClassesThat().resideInAnyPackage("..persistence..", "..service..");

        rule.check(importedClasses);
        
    }

    @Test
    public void verificarDependenciasDaCamadaPersistencia() {

        ArchRule rule = noClasses()
        .that().resideInAPackage("..persistence..")
        .should().dependOnClassesThat().resideInAnyPackage("..service..");

        rule.check(importedClasses);
        
    }

    @Test
    public void verificarNomesClassesCamadaPersistencia() {

        ArchRule rule = classes()
        .that().haveSimpleNameEndingWith("Dao")
        .should().resideInAnyPackage("..persistence..");

        rule.check(importedClasses);
        
    }

    @Test
    public void verificarImportCamadaPersistencia() {

        ArchRule rule = classes()
        .that().implement(Dao.class)
        .should().haveSimpleNameEndingWith("Dao");

        rule.check(importedClasses);
        
    }

    @Test
    public void verificarDependenciaCiclica() {

        ArchRule rule = slices()
        .matching("br.com.fas.(*)..").should().beFreeOfCycles();

        rule.check(importedClasses);
        
    }

    @Test
    public void verificarViolacaoCamadas() {

        ArchRule rule = Architectures.layeredArchitecture()
        .layer("Service").definedBy("..service..")
        .layer("Persistence").definedBy("..persistence..")
        .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service");

        rule.check(importedClasses);
        
    }
    
    
    
}

