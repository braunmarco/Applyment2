package my.cvmanager.web;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named("helloBean") // Name für JSF
@RequestScoped
public class HelloBean {
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public void sayHello() {
        System.out.println("Hallo " + name + "!");
    }
}
