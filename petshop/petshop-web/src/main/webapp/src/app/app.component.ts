import { Component } from '@angular/core';
import { OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { PetshopService } from './petshop.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit {
  title = 'Demo Pet Shop';
  animals = [];
  total = 0;
  contact = {};

  private petshopService: PetshopService;


  constructor(private $petshopService: PetshopService) {
    this.petshopService = $petshopService;
  }

  ngOnInit(): void {
    this.petshopService.getContactInformation().then($info => {
      this.contact = $info;
    });
    this.petshopService.getAnimals().then($animals => {
      let appComp : AppComponent = this;
      this.animals = $animals;
      this.animals.forEach(a => a.onChange(() => appComp.updateTotal()));
      this.updateTotal();
      });
  }

  private updateTotal(){
      this.total = this.animals.map(a => a.totalPrice).reduce( (accu, value) => accu+value);
  }

}
