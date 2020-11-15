## Studying Dynamic programming
## Sources :
- [MIT - Introduction-to-algorithms-fall-2011](https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-006-introduction-to-algorithms-fall-2011/lecture-videos/)
- [DP I](https://www.youtube.com/watch?v=OQ5jsbhAv_M&t=1811s)
- [DP II](https://www.youtube.com/watch?v=ENyox7kNKeY)
- [DP III](https://www.youtube.com/watch?v=ocZMDMZwhCY)
- [DP IV](https://www.youtube.com/watch?v=tp4_UXaVyx8)

## DP Principles :
- Careful brute force
- Guessing + recursion + memoization
- Most problems solved by the DP can be designed as "shortes paths in some DAG" (Direct Acyclic Graph)
- time compexity = #subproblems * (time/subprblem) (recursion calls should be treated as O(1))

## DP "easy" steps :
1. Define subproblems then the #subproblems analytics
2. Guess (part of solution) then the #choices
3. Relate subproblem solutions
4. Recurse & memoize OR build DP table bottom-up
5. Solve original problem

NB : the most difficult ones are (1) and (2)

## Done ?
- Text justification using DP
