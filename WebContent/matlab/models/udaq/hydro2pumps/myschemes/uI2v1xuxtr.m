function u = uI2v1xuxtr(h1,h2,w)
           %First transfers x and control limits
           %uI2v1x Computes control action for double integrator based on
           %directions of v1, and x
           %Inverse transfer of control limits
           

           
           global alpha1 alpha2  U m1c m2c; 
           
    
          x = h2-w;
          y = .1627300000e-1*(h1-1.*h2)^(1/2)-.8679900000e-2*h2^(1/2);
         
          w1 = 1.284507811*w;
                     
          h1l=m1c*w1+(1-m1c)*h1;
          h2l=m2c*w+(1-m2c)*h2;
                      
          Khi = 8.136500000/(h1l-1.*h2l)^(1/2);
          fhi = -.1500000000e-12*(1514267980.*(h1l-1.*h2l)^(1/2)*h2l^(1/2)-941653418.*h2l+470826709.*h1l)/(h1l-1.*h2l)^(1/2)/h2l^(1/2);
          Uf(1) = Khi*U(1)+fhi;
          Uf(2) = Khi*U(2)+fhi;
           
          Uf1=Uf(1);Uf2=Uf(2);
          
          if (Uf(1)*Uf(2)>0) 
              if (h1+h2>w+w1)
                   u=U(1);
                   return;
              else
                   u=U(2);
                   return;
              end
          end
           
          j= (3-sign(y))/2;
           
          if (abs(y) <= abs(Uf(j)/alpha1) )
              usolcer = -alpha1*x*alpha2+y*alpha1+y*alpha2;
              u=usolcer;
              u=(u-fhi)/Khi;
              return
         else
              u=(1/2)*(2*y*alpha1^2*Uf(j)-2*alpha2*x*alpha1^2*Uf(j)+alpha2*Uf(j)^2+alpha2*y^2*alpha1^2)/(alpha1^2*y);
              u=(u-fhi)/Khi;
              return
          end
          