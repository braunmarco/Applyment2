#!/bin/bash

# Array mit "Titel | Beschreibung"
issues=(
  "Epic: Projektgrundgerüst & Setup | Dieses Epic enthält alle Aufgaben für die Einrichtung des Projekts."
  "Git-Repository anlegen und Grundstruktur erstellen | Repository auf GitHub anlegen und lokal klonen. Standardordnerstruktur für Java-Projekt anlegen (src/main/java, src/main/resources). README.md erstellen."
  "Maven/Gradle-Projekt mit Dependencies für Hibernate, JSF, PrimeFaces anlegen | pom.xml oder build.gradle konfigurieren. Dependencies: Hibernate, JPA, PrimeFaces, JSF-Implementierung (z. B. Mojarra)."
  "Tomcat/WildFly als Entwicklungsserver konfigurieren | Application Server lokal installieren. Deployment-Konfiguration für Projekt erstellen."
  "Lokale Datenbank erstellen | PostgreSQL oder MySQL Datenbank anlegen (Name: cvdb). Benutzer + Passwort einrichten."

  "Epic: Datenmodell & Persistence Layer | Dieses Epic enthält die Implementierung der Entities und des Persistence Layers."
  "Entity User anlegen | @Entity-Klasse User mit Feldern: firstName, lastName, email, phone, location, summary. @OneToMany zu Position."
  "Entity Position anlegen | @Entity-Klasse Position mit Feldern: title, company, location, startDate, endDate, description. @ManyToOne zu User. @OneToMany zu Technology."
  "Entity Technology anlegen | @Entity-Klasse Technology mit Feldern: name, level. @ManyToOne zu Position."
  "Relationen zwischen Entities definieren | User ↔ Position (OneToMany/ManyToOne). Position ↔ Technology (OneToMany/ManyToOne)."
  "persistence.xml konfigurieren | DB-Connection, Hibernate-Dialect, hbm2ddl.auto=update, show_sql=true."
  "Repository-Klassen für CRUD erstellen | DAO-Klassen mit EntityManager für User, Position, Technology. Methoden: create, read, update, delete."

  "Epic: Service Layer | Implementierung der Business-Logik-Schicht."
  "UserService erstellen | Service-Klasse für Benutzerverwaltung (Create, Update, Delete)."
  "PositionService erstellen | Service-Klasse für Positionsverwaltung inkl. Technologien."
  "Validierung der Eingaben mit Bean Validation | Java Bean Validation (@NotNull, @Size etc.) einsetzen."

  "Epic: UI mit JSF + PrimeFaces | Alle UI-Seiten und Managed Beans erstellen."
  "profile.xhtml erstellen (Persönliche Daten) | Formular mit PrimeFaces-Komponenten. An Managed Bean binden."
  "positions.xhtml erstellen (Liste, Add/Edit/Delete) | DataTable mit Dialog-Formularen."
  "Technologien pro Position verwalten | Eingabefelder für Technologien (PrimeFaces Chips/SelectMany)."

  "Epic: CV-Export | Lebenslauf als PDF exportieren."
  "cv-preview.xhtml erstellen (Vorschau) | HTML-Template mit Platzhaltern für Daten."
  "PDF-Export-Service implementieren | iText oder Flying Saucer nutzen."
  "Download-Button einfügen | Button in UI zum Herunterladen des PDFs."
)

# Issues in GitHub anlegen
for entry in "${issues[@]}"; do
  IFS="|" read -r title body <<< "$entry"
  echo "Erstelle Issue: $title"
  gh issue create --title "$title" --body "$body"
done
