import { Injectable } from '@angular/core';
import { Headers, Http } from '@angular/http';
import { Animal } from './animal';
// import 'rxjs/add/operator/toPromise';

  const baseUrl = 'http://localhost:8080/petshop';  // URL to web api

@Injectable()
export class PetshopService {

    constructor(private http: Http) { }

  getAnimals(): Promise<Animal[]> {
    return this.http.get(baseUrl + "/animals")
                    .toPromise()
                    .then(response => response.json().map(obj => new Animal(this, obj)) as Animal[])
                    .catch(this.handleError);
  }

  getQuote(species:string, quantity:number): Promise<object> {
    return this.http.get(baseUrl + "/animals/" + species + "/price/?quantity="+quantity)
                    .toPromise()
                    .then(response => response.json())
                    .catch(this.handleError);

  }


   getRoot(): Promise<object> {
    return this.http.get(baseUrl + '/groups')
                       .toPromise()
                       .then(response => response)
                       .catch(this.handleError);
   }


   dump(obj: object){
     let str = "{";

     for (let p in obj){
       str += p + ": " + obj[p] + "\n";
     }
     str += "}";
     return str;
   }

   getGroup(name: string): Promise<object> {
        return this.http.get(baseUrl + '/groups')
                           .toPromise()
                           .then(response => {
                                return response
                                // let groupData = response.json().groups.filter(obj => obj.name==name);
                                //return (groupData.length > 0)? new Group(groupData[0]) : null;
                              })
                           .catch(this.handleError);
   }

  insertEntry(groupName: string, entry: object): Promise<void> {
    return this.http.post(baseUrl + "/groups/"+groupName+"/entries", entry)
                    .toPromise()
                    .catch(this.handleError);
  }
  updateEntry(groupName: string, id: string, entry: object): Promise<void> {
    return this.http.patch(baseUrl + "/groups/"+groupName+"/entries/" + id, entry)
                    .toPromise()
                    .catch(this.handleError);
  }


  private cloneEntry(entry): Object {
    let result = {};
    this.copyEntry(entry, result);
    return result;
  }

  private copyEntry(from, to){
    for(let x in from){
      to[x] = from[x];
    }
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }
}

