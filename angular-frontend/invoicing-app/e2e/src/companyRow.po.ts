import {browser, by, element, ElementArrayFinder, ElementFinder, WebElement} from 'protractor';

export class CompanyRow {

  constructor(private companyRow: ElementFinder) { }

  deleteBtn(): WebElement {
    return this.companyRow.element(by.css('.btn-danger'))
  }

  async taxIdentificationValue(): Promise<string> {
    return this.companyRow.element(by.id('taxIdentification')).getText()
  }

  async nameValue(): Promise<string> {
    return this.companyRow.element(by.id('name')).getText()
  }

  async addressValue(): Promise<string> {
    return this.companyRow.element(by.id('address')).getText()
  }

  async pensionInsuranceValue(): Promise<string> {
    return this.companyRow.element(by.id('pensionInsurance')).getText()
  }

  async healthInsuranceValue(): Promise<string> {
    return this.companyRow.element(by.id('healthInsurance')).getText()
  }

  async assertRowValues(taxIdentification: string,
                        name: string,
                        address: string,
                        pensionInsurance: string,
                        healthInsurance: string) {
    expect(await this.taxIdentificationValue()).toEqual(taxIdentification)
    expect(await this.nameValue()).toEqual(name)
    expect(await this.addressValue()).toEqual(address)
    expect(await this.pensionInsuranceValue()).toEqual(pensionInsurance)
    expect(await this.healthInsuranceValue()).toEqual(healthInsurance)
  }

  async updateCompany(taxIdentification: string,
                      name: string,
                      address: string,
                      pensionInsurance: number,
                      healthInsurance: number) {
    await this.editBtn().click()

    await this.updateTaxIdentificationInput().clear()
    await this.updateTaxIdentificationInput().sendKeys(taxIdentification)

    await this.updateNameInput().clear()
    await this.updateNameInput().sendKeys(name)

    await this.updateAddressInput().clear()
    await this.updateAddressInput().sendKeys(address)

    await this.updatePensionInsuranceInput().clear()
    await this.updatePensionInsuranceInput().sendKeys(pensionInsurance)

    await this.updateHealthInsuranceInput().clear()
    await this.updateHealthInsuranceInput().sendKeys(healthInsurance)

    await this.confirmUpdateCompanyBtn().click()
  }

  private confirmUpdateCompanyBtn() {
    return this.companyRow.element(by.css('.btn-success'));   // DO WERYFIKACJI
  }

  private updateHealthInsuranceInput() {
    return this.companyRow.element(by.css('input[name=healthInsurance]'));
  }

  private updatePensionInsuranceInput() {
    return this.companyRow.element(by.css('input[name=pensionInsurance]'));
  }

  private updateAddressInput() {
    return this.companyRow.element(by.css('input[name=address]'));
  }

  private updateNameInput() {
    return this.companyRow.element(by.css('input[name=name]'));
  }

  private updateTaxIdentificationInput() {
    return this.companyRow.element(by.css('input[name=taxIdentification]'));
  }

  private editBtn() {
    return this.companyRow.element(by.css('.btn-secondary'));   //DO WERYFIKACJI
  }
}
