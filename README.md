API Design for a vending machine, allowing users with a "seller" role to add, update or remove products, while users with a "buyer" role can deposit coins into the machine and make purchases.
The vending machine should only accept 5, 10, 20, 50 and 100 cent coins.

Features:
• REST API should be implemented consurning and producing .application/jsor, 
• Implemented product model with amountAvailable, cost, productName and sellerld fields 
• Implemented user model with username, password, deposit and role fields 
• Implemented CRUD for users (POST shouldn't require authentication) 
• Implemented CRUD for a product model (GET can be called by anyone, while POST. PUT and DELETE can be called only by the s.. user who created the product) 
• Implemented /deposit endpoint so users with a ”buyer. role can deposit 5, 10, 20, 50 and 100 cent coins into their vending machine account 
• Implemented /buy endpoint (accepts productld, arnount of products) so users with a "buyer role can buy products with the money they've deposited. API should return total they've spent, products they've purchased and their change if there's any (in 5, 10, 20, 50 and 100 cent coins) 
• Implemented /reset endpoint so users with a 'buyer" role can reset their deposit • Take time to think about possible edge cases and access issues that should be solved 
