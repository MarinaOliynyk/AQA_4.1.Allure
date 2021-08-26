package ru.netology.delivery.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

class CardOrderTest {
    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    DataGenerator.UserInfo user = DataGenerator.Registration.generateValidUser();
    String firstMeetingDate = DataGenerator.generateDate(3);
    String secondMeetingDate = DataGenerator.generateDate(5);
    String invalidMeetingDate = DataGenerator.generateDate(0);

    //тест на изначальную дату доступную в календаре
    @Test
    void shouldHappyPathTestDate() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(withText("Запланировать")).click();
        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=success-notification] .notification__content ").shouldBe(visible).shouldHave(text(firstMeetingDate));
    }

    //тест с перепланированием даты
    @Test
    void shouldHappyPathTestNewDate() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(withText("Запланировать")).click();
        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=success-notification] .notification__content ").shouldHave(text(firstMeetingDate));
        $("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
        $("[data-test-id=date] input").setValue(secondMeetingDate);
        $(withText("Запланировать")).click();
        $(withText("Необходимо подтверждение")).shouldBe(visible, Duration.ofSeconds(15));
        $(withText("У вас уже запланирована встреча на другую дату.")).shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=replan-notification] button.button").click();
        $(withText("Успешно!")).shouldBe(visible);
        $("[data-test-id=success-notification] .notification__content ").shouldHave(text(secondMeetingDate));
    }


    @Test
    void shouldTestFieldCity() {
        $("[data-test-id=city] input").setValue("Сочи").pressEnter();
        $("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(withText("Запланировать")).click();
        $(".input_invalid .input__sub").shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldTestFieldData() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
        $("[data-test-id=date] input").setValue(invalidMeetingDate);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(withText("Запланировать")).click();
        $(".input_invalid .input__sub").shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldTestFieldName() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue("Oliynyk Marina");
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        $(withText("Запланировать")).click();
        $(".input_invalid .input__sub").shouldHave(exactText("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    void shouldTestFieldPhone() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(" ");
        $("[data-test-id=agreement]").click();
        $(withText("Запланировать")).click();
        $(".input_invalid .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldTestFieldAgreement() {
        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").sendKeys(Keys.CONTROL + "a" + Keys.DELETE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $(withText("Запланировать")).click();
        $("[data-test-id='agreement'].input_invalid ").shouldBe(visible);
        $(".input_invalid .checkbox__text").shouldHave(exactText("Я соглашаюсь с условиями обработки и использования моих персональных данных"));
    }
}


