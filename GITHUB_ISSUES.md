# 📋 GitHub Issue Vorlage – CV-Builder Projekt (Java + Hibernate + JSF)

## Epic 1 – Projektgrundgerüst & Setup
**Beschreibung:**
Dieses Epic enthält alle Aufgaben für die Einrichtung des Projekts.

- [ ] #1 Git-Repository anlegen und Grundstruktur erstellen
- [ ] #2 Maven/Gradle-Projekt mit Dependencies für Hibernate, JSF, PrimeFaces anlegen
- [ ] #3 Tomcat/WildFly als Entwicklungsserver konfigurieren
- [ ] #4 Lokale Datenbank erstellen (PostgreSQL oder MySQL)

---

### #1 Git-Repository anlegen und Grundstruktur erstellen
Repository auf GitHub anlegen und lokal klonen.  
Standardordnerstruktur für Java-Projekt anlegen (`src/main/java`, `src/main/resources`).  
README.md erstellen.

### #2 Maven/Gradle-Projekt mit Dependencies für Hibernate, JSF, PrimeFaces anlegen
`pom.xml` oder `build.gradle` konfigurieren.  
Dependencies: Hibernate, JPA, PrimeFaces, JSF-Implementierung (z. B. Mojarra).

### #3 Tomcat/WildFly als Entwicklungsserver konfigurieren
Application Server lokal installieren.  
Deployment-Konfiguration für Projekt erstellen.

### #4 Lokale Datenbank erstellen
PostgreSQL oder MySQL Datenbank anlegen (Name: `cvdb`).  
Benutzer + Passwort einrichten.

---

## Epic 2 – Datenmodell & Persistence Layer
**Beschreibung:**
Dieses Epic enthält die Implementierung der Entities und des Persistence Layers.

- [ ] #5 Entity User anlegen
- [ ] #6 Entity Position anlegen
- [ ] #7 Entity Technology anlegen
- [ ] #8 Relationen zwischen Entities definieren
- [ ] #9 persistence.xml konfigurieren
- [ ] #10 Repository-Klassen für CRUD erstellen

---

### #5 Entity User anlegen
@Entity-Klasse `User` mit Feldern:  
- firstName, lastName, email, phone, location, summary  
@OneToMany zu Position

### #6 Entity Position anlegen
@Entity-Klasse `Position` mit Feldern:  
- title, company, location, startDate, endDate, description  
@ManyToOne zu User  
@OneToMany zu Technology

### #7 Entity Technology anlegen
@Entity-Klasse `Technology` mit Feldern:  
- name, level  
@ManyToOne zu Position

### #8 Relationen zwischen Entities definieren
- User ↔ Position (OneToMany/ManyToOne)  
- Position ↔ Technology (OneToMany/ManyToOne)

### #9 persistence.xml konfigurieren
DB-Connection, Hibernate-Dialect, hbm2ddl.auto=update, show_sql=true

### #10 Repository-Klassen für CRUD erstellen
DAO-Klassen mit EntityManager für User, Position, Technology.  
Methoden: create, read, update, delete.

---

## Epic 3 – Service Layer
**Beschreibung:**
Implementierung der Business-Logik-Schicht.

- [ ] #11 UserService erstellen
- [ ] #12 PositionService erstellen
- [ ] #13 Validierung der Eingaben mit Bean Validation

---

## Epic 4 – UI mit JSF + PrimeFaces
**Beschreibung:**
Alle UI-Seiten und Managed Beans erstellen.

- [ ] #14 profile.xhtml erstellen (Persönliche Daten)
- [ ] #15 positions.xhtml erstellen (Liste, Add/Edit/Delete)
- [ ] #16 Technologien pro Position verwalten

---

## Epic 5 – CV-Export
**Beschreibung:**
Lebenslauf als PDF exportieren.

- [ ] #17 cv-preview.xhtml erstellen (Vorschau)
- [ ] #18 PDF-Export-Service implementieren
- [ ] #19 Download-Button einfügen

---

## Epic 6 – Tests & Feinschliff
**Beschreibung:**
Tests schreiben und UI verbessern.

- [ ] #20 Unit-Tests für Services
- [ ] #21 Integrationstests für Hibernate
- [ ] #22 UI-Tests mit Selenium
- [ ] #23 CSS-Anpassungen und Fehlerseiten
