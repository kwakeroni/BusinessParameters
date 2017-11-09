import { Input } from '@angular/core';
import { PetshopService } from './petshop.service';


  export class Animal {
    service: PetshopService;
    species: string;
    unitPrice: number;
    salePercentage: number;
    salePrice: number;
    _quantity: number;
    totalPrice: number;
    onChangeFunction = function(){};

    constructor($service : PetshopService, data) {
      this.service = $service;
      this.update(data);
    }

  @Input()
  set quantity(q: number) {
    this.fetchUpdate(q);
    // this._quantity = q;
    // this.totalPrice = this.price * this._quantity;
  }

  get quantity(){
    return this._quantity;
  }

  private fetchUpdate(q: number){
    this.service.getQuote(this.species, q)
      .then(result => this.update(result));
  }

  private update(data){
      this.species = data.species;
      this.unitPrice = data.unitPrice;
      this.salePercentage = data.salePercentage;
      this.salePrice = data.salePrice;
      this._quantity = (data.quantity)? data.quantity : 0;
      let actualPrice = (this.salePrice)? this.salePrice : this.unitPrice;
      this.totalPrice = (data.totalPrice)? data.totalPrice : ( actualPrice * this._quantity);
      this.onChangeFunction();
  }

  onChange(func){
    let current = this.onChangeFunction;
    this.onChangeFunction = function(){
      func();
      current();
    };
  }
}
