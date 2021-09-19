## Setup

The `rpm` utility uses GPG keys to sign packages and a collected of public keys to verify the packages. We specify a URL to MongoDB's GPG key and use it to verify the packages from the repository we are adding.

```
sudo vim /etc/yum.repos.d/mongodb.repo

[Mongodb]
name=MongoDB Repository
baseurl=https://repo.mongodb.org/yum/redhat/8/mongodb-org/4.4/x86_64/
gpgcheck=1
enabled=1
gpgkey=https://www.mongodb.org/static/pgp/server-4.4.asc

```

Then we simply install MongoDB and `dnf` will do the check for us

```
sudo dnf install mongodb-org mongodb-mongosh

Importing GPG key 0x90CFB1F5:
 Userid     : "MongoDB 4.4 Release Signing Key <packaging@mongodb.com>"
 Fingerprint: 2069 1EEC 3521 6C63 CAF6 6CE1 6564 08E3 90CF B1F5
 From       : https://www.mongodb.org/static/pgp/server-4.4.asc
Is this ok [y/N]: y

Key imported successfully
Running transaction check
Transaction check succeeded.
Running transaction test
Transaction test succeeded.
```
Increasing number of file descriptors up to `64000`

```
ulimit -n 64000
```

Lastly, start the service
```
sudo systemctl start mongod
```

## Experiment 1

```
mongosh
```

#### Insert

```
db.inventory.insertMany([
   { item: "journal", qty: 25, tags: ["blank", "red"], size: { h: 14, w: 21, uom: "cm" } },
   { item: "mat", qty: 85, tags: ["gray"], size: { h: 27.9, w: 35.5, uom: "cm" } },
   { item: "mousepad", qty: 25, tags: ["gel", "blue"], size: { h: 19, w: 22.85, uom: "cm" } }
])
```
Find all with `qty == 25` 
```
db.inventory.find({"qty": 25})
[
  {
    _id: ObjectId("61472d61cb224f658990e70f"),
    item: 'journal',
    qty: 25,
    tags: [ 'blank', 'red' ],
    size: { h: 14, w: 21, uom: 'cm' }
  },
  {
    _id: ObjectId("61472d61cb224f658990e711"),
    item: 'mousepad',
    qty: 25,
    tags: [ 'gel', 'blue' ],
    size: { h: 19, w: 22.85, uom: 'cm' }
  }
]

```

#### Query

Working with the following database:
```
db.inventory.insertMany([
   { item: "journal", qty: 25, size: { h: 14, w: 21, uom: "cm" }, status: "A" },
   { item: "notebook", qty: 50, size: { h: 8.5, w: 11, uom: "in" }, status: "A" },
   { item: "paper", qty: 100, size: { h: 8.5, w: 11, uom: "in" }, status: "D" },
   { item: "planner", qty: 75, size: { h: 22.85, w: 30, uom: "cm" }, status: "D" },
   { item: "postcard", qty: 45, size: { h: 10, w: 15.25, uom: "cm" }, status: "A" }
]);
```
Find all documents with status `"A"`
```
db.inventory.find( { status: "A" } )

db.inventory.find( { status: "A" } )
[
  {
    _id: ObjectId("61472f05cb224f658990e712"),
    item: 'journal',
    qty: 25,
    size: { h: 14, w: 21, uom: 'cm' },
    status: 'A'
  },
  {
    _id: ObjectId("61472f05cb224f658990e713"),
    item: 'notebook',
    qty: 50,
    size: { h: 8.5, w: 11, uom: 'in' },
    status: 'A'
  },
  {
    _id: ObjectId("61472f05cb224f658990e716"),
    item: 'postcard',
    qty: 45,
    size: { h: 10, w: 15.25, uom: 'cm' },
    status: 'A'
  }
]
```

Or all items that use `inches` as unit of length

```
db.inventory.find( { "size.uom": { $in: ["in"] } } )

[
  {
    _id: ObjectId("61472f05cb224f658990e713"),
    item: 'notebook',
    qty: 50,
    size: { h: 8.5, w: 11, uom: 'in' },
    status: 'A'
  },
  {
    _id: ObjectId("61472f05cb224f658990e714"),
    item: 'paper',
    qty: 100,
    size: { h: 8.5, w: 11, uom: 'in' },
    status: 'D'
  }
]
```

#### Update

```
db.inventory.insertMany( [
   { item: "canvas", qty: 100, size: { h: 28, w: 35.5, uom: "cm" }, status: "A" },
   { item: "journal", qty: 25, size: { h: 14, w: 21, uom: "cm" }, status: "A" },
   { item: "mat", qty: 85, size: { h: 27.9, w: 35.5, uom: "cm" }, status: "A" },
   { item: "mousepad", qty: 25, size: { h: 19, w: 22.85, uom: "cm" }, status: "P" },
   { item: "notebook", qty: 50, size: { h: 8.5, w: 11, uom: "in" }, status: "P" },
   { item: "paper", qty: 100, size: { h: 8.5, w: 11, uom: "in" }, status: "D" },
   { item: "planner", qty: 75, size: { h: 22.85, w: 30, uom: "cm" }, status: "D" },
   { item: "postcard", qty: 45, size: { h: 10, w: 15.25, uom: "cm" }, status: "A" },
   { item: "sketchbook", qty: 80, size: { h: 14, w: 21, uom: "cm" }, status: "A" },
   { item: "sketch pad", qty: 95, size: { h: 22.85, w: 30.5, uom: "cm" }, status: "A" }
] );
```

Changing quantity and unit of length of `canvas`

```
db.inventory.updateOne(
   { item: "canvas" },
   {
     $set: { "size.uom": "in", qty: 1 },
     $currentDate: { lastModified: true }
   }
)
{
  acknowledged: true,
  insertedId: null,
  matchedCount: 1,
  modifiedCount: 1,
  upsertedCount: 0
}
```

Checking the updated canvas document

```
db.inventory.find({ "item": "canvas" })
[
  {
    _id: ObjectId("6147322ccb224f658990e717"),
    item: 'canvas',
    qty: 1,
    size: { h: 28, w: 35.5, uom: 'in' },
    status: 'A',
    lastModified: ISODate("2021-09-19T12:51:06.207Z")
  }
]

```


#### Delete

```
db.inventory.insertMany( [
...    { item: "journal", qty: 25, size: { h: 14, w: 21, uom: "cm" }, status: "A" },
...    { item: "notebook", qty: 50, size: { h: 8.5, w: 11, uom: "in" }, status: "P" },
...    { item: "paper", qty: 100, size: { h: 8.5, w: 11, uom: "in" }, status: "D" },
...    { item: "planner", qty: 75, size: { h: 22.85, w: 30, uom: "cm" }, status: "D" },
...    { item: "postcard", qty: 45, size: { h: 10, w: 15.25, uom: "cm" }, status: "A" },
... ] );
```


Delete all inventory documents with status `"A"`
```
db.inventory.deleteMany({ status: "A" })
{ acknowledged: true, deletedCount: 2 }
```

Delete all inventory documents

```
db.inventory.deleteMany({})
{ acknowledged: true, deletedCount: 3 }

```

Querying all inventory documents returns empty
```
db.inventory.find({})


```

#### Bulk Write

```
db.characters.insertMany([
... { "_id" : 1, "char" : "Brisbane", "class" : "monk", "lvl" : 4 },
... { "_id" : 2, "char" : "Eldon", "class" : "alchemist", "lvl" : 3 },
... { "_id" : 3, "char" : "Meldane", "class" : "ranger", "lvl" : 3 }
... ])
{ acknowledged: true, insertedIds: { '0': 1, '1': 2, '2': 3 } }
```

Here we try to insert many rows in bulk with an *unordered* list of operations. The operations are executed in parallel (faster). If one operation fail, the rest will continue being executed. 

```
try {
   db.characters.bulkWrite([
      { insertOne: { "document": { "_id": 4, "char": "Dithras", "class": "barbarian", "lvl": 4 } } },
      { insertOne: { "document": { "_id": 4, "char": "Taeln", "class": "fighter", "lvl": 3 } } },
      { updateOne : {
         "filter" : { "char" : "Eldon" },
         "update" : { $set : { "status" : "Critical Injury" } }
      } },
      { deleteOne : { "filter" : { "char" : "Brisbane"} } },
      { replaceOne : {
         "filter" : { "char" : "Meldane" },
         "replacement" : { "char" : "Tanys", "class" : "oracle", "lvl": 4 }
      } }
   ], { ordered : false } );
} catch (e) {
   print(e);
}
```
This threw a `MongoBulkWriteError: E11000 duplicate key error collection: test.characters index: _id_ dup key: { _id: 4 }` exception but still continued to process the rest of the operations 


```
db.characters.find({})
[
  {
    _id: 2,
    char: 'Eldon',
    class: 'alchemist',
    lvl: 3,
    status: 'Critical Injury'
  },
  { _id: 3, char: 'Tanys', class: 'oracle', lvl: 4 },
  { _id: 4, char: 'Dithras', class: 'barbarian', lvl: 4 }
]
```

## Experiment 2

### Total Price Per Customer

```
db.orders.insertMany([
   { _id: 1, cust_id: "Ant O. Knee", ord_date: new Date("2020-03-01"), price: 25, items: [ { sku: "oranges", qty: 5, price: 2.5 }, { sku: "apples", qty: 5, price: 2.5 } ], status: "A" },
   { _id: 2, cust_id: "Ant O. Knee", ord_date: new Date("2020-03-08"), price: 70, items: [ { sku: "oranges", qty: 8, price: 2.5 }, { sku: "chocolates", qty: 5, price: 10 } ], status: "A" },
   { _id: 3, cust_id: "Busby Bee", ord_date: new Date("2020-03-08"), price: 50, items: [ { sku: "oranges", qty: 10, price: 2.5 }, { sku: "pears", qty: 10, price: 2.5 } ], status: "A" },
   { _id: 4, cust_id: "Busby Bee", ord_date: new Date("2020-03-18"), price: 25, items: [ { sku: "oranges", qty: 10, price: 2.5 } ], status: "A" },
   { _id: 5, cust_id: "Busby Bee", ord_date: new Date("2020-03-19"), price: 50, items: [ { sku: "chocolates", qty: 5, price: 10 } ], status: "A"},
   { _id: 6, cust_id: "Cam Elot", ord_date: new Date("2020-03-19"), price: 35, items: [ { sku: "carrots", qty: 10, price: 1.0 }, { sku: "apples", qty: 10, price: 2.5 } ], status: "A" },
   { _id: 7, cust_id: "Cam Elot", ord_date: new Date("2020-03-20"), price: 25, items: [ { sku: "oranges", qty: 10, price: 2.5 } ], status: "A" },
   { _id: 8, cust_id: "Don Quis", ord_date: new Date("2020-03-20"), price: 75, items: [ { sku: "chocolates", qty: 5, price: 10 }, { sku: "apples", qty: 10, price: 2.5 } ], status: "A" },
   { _id: 9, cust_id: "Don Quis", ord_date: new Date("2020-03-20"), price: 55, items: [ { sku: "carrots", qty: 5, price: 1.0 }, { sku: "apples", qty: 10, price: 2.5 }, { sku: "oranges", qty: 10, price: 2.5 } ], status: "A" },
   { _id: 10, cust_id: "Don Quis", ord_date: new Date("2020-03-23"), price: 25, items: [ { sku: "oranges", qty: 10, price: 2.5 } ], status: "A" }
])
{
  acknowledged: true,
  insertedIds: {
    '0': 1,
    '1': 2,
    '2': 3,
    '3': 4,
    '4': 5,
    '5': 6,
    '6': 7,
    '7': 8,
    '8': 9,
    '9': 10
  }
}
```

**Map:** for each `cust_id` emit `price`

```
var mapFunction1 = function() {
   emit(this.cust_id, this.price);
};
```

**Reduce:** for each `keyCustId` sum `valuesPrices`

```
var reduceFunction1 = function(keyCustId, valuesPrices) {
   return Array.sum(valuesPrices);
};
```

Grouping all prices with the same `cust_id` together in the same array and summing each array of prices

```
db.orders.mapReduce(
   mapFunction1,
   reduceFunction1,
   { out: "map_reduce_example" }
)
```

Outputting the resulting map-reduce operation stored in the `map_reduce_example` collection

```
db.map_reduce_example.find().sort( { _id: 1 })
[
  { _id: 'Ant O. Knee', value: 95 },
  { _id: 'Busby Bee', value: 125 },
  { _id: 'Cam Elot', value: 60 },
  { _id: 'Don Quis', value: 155 }
]
```
### Order, Total Quantity & Average Quantity Per Item

**Map:** for each item emit 1 (count) and quantity
```
 var mapFunction2 = function() {
    for (var idx = 0; idx < this.items.length; idx++) {
       var key = this.items[idx].sku;
       var value = { count: 1, qty: this.items[idx].qty };
       emit(key, value);
    }
};
```

**Reduce:** for each sku count and sum the quantities
```
var reduceFunction2 = function(keySKU, countObjVals) {
   reducedVal = { count: 0, qty: 0 };
   for (var idx = 0; idx < countObjVals.length; idx++) {
       reducedVal.count += countObjVals[idx].count;
       reducedVal.qty += countObjVals[idx].qty;
   }
   return reducedVal;
};
```

**Finalize:** for each sku calculate average quantity per item
```
var finalizeFunction2 = function (key, reducedVal) {
  reducedVal.avg = reducedVal.qty/reducedVal.count;
  return reducedVal;
};
```


```
db.orders.mapReduce(
   mapFunction2,
   reduceFunction2,
   {
     out: { merge: "map_reduce_example2" },
     query: { ord_date: { $gte: new Date("2020-03-01") } },
     finalize: finalizeFunction2
   }
 );
```

Find all orders, their total quantity and average quantity per item *after 2020-03-01*

```
db.map_reduce_example2.find().sort( { _id: 1 })
[
  { _id: 'apples', value: { count: 4, qty: 35, avg: 8.75 } },
  { _id: 'carrots', value: { count: 2, qty: 15, avg: 7.5 } },
  { _id: 'chocolates', value: { count: 3, qty: 15, avg: 5 } },
  { _id: 'oranges', value: { count: 7, qty: 63, avg: 9 } },
  { _id: 'pears', value: { count: 1, qty: 10, avg: 10 } }
]

```

### Customers And Their Max Quantity Orders > 1

```
var mapFunction3 = function() {
   emit(this.cust_id, this.items.length);
};
```


```
var reduceFunction3 = function(keyCustId, itemsLength) {
   return Math.max(...itemsLength)
};
```

```
db.orders.mapReduce( mapFunction3, reduceFunction3, { out: "map_reduce_example3" })
{ result: 'map_reduce_example3', ok: 1 }
```

```
db.map_reduce_example3.find().sort( { _id: 1 } )
[
  { _id: 'Ant O. Knee', value: 2 },
  { _id: 'Busby Bee', value: 2 },
  { _id: 'Cam Elot', value: 2 },
  { _id: 'Don Quis', value: 3 }
]
```
