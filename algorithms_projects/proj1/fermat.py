import random


def prime_test(N, k):
	# This is main function, that is connected to the Test button. You don't need to touch it.
	return fermat(N,k), miller_rabin(N,k)


def mod_exp(x, y, N):
    
    """This function is the recursive implemenation of modular exponentiation. 
    It recursively breaks numbers up into powers of 2."""
    
    if y == 0:
        return 1
    z = mod_exp(x, y//2, N)
    if y%2==0:
        return (z**2)%N
    else:
        return (x*z*z)%N


def fprobability(k):
    return float(1-(1/2)**k) #The probability of getting a false positive for any given 1<=a<N is approx. 1/2. 


def mprobability(k):
    return float(1-(1/4)**k) #The probability of getting a false positive for any given 1<=a<N is approx. 1/4.


def fermat(N,k):
    
    for _ in range(k): 
        a = random.randint(1,N-1)
        if mod_exp(a,N-1,N) != 1: 
            return 'composite' #if any of our checks do not =1 (mod N) then we are sure N is composite.
    return 'prime' #if a^N-1 = 1 (mod N) for all k a's, then we can conclude that N is prime.


def miller_rabin(N,k):
    
    for _ in range(k):
        a = random.randint(1,N-1)
        if mod_exp(a,N-1,N) != 1:
            return 'composite'
        else:
            b = 2*(N-1)
            while b%2==0: #This loop continues while we can take square roots.
                b = b/2 #Set the new b value at the start of the loop so that it would run if the final b is odd.
                temp = mod_exp(a,b,N)
                print(a, b, N, temp)
                if temp == N-1: #equivalent to -1 (mod N)
                    break
                if temp != 1:
                    return 'composite'
                
    return 'prime'
