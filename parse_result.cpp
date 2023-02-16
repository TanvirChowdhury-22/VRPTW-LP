#include <algorithm>
#include <glpk.h>
#include <iostream>

//$ apt-get install libglpk-dev

int main(int argc, char *argv[]) {

  glp_tran *tran = nullptr;
  glp_prob *P;

  try {
    if (argc < 3) {
      throw "Please provide a filename";
    }

    std::string modelFileLocation = argv[1];
    std::string dataFileLocation = argv[2];

    int ret;
    
    P = glp_create_prob();
    //glp_read_lp(P, NULL, fileLocation.c_str());
    tran = glp_mpl_alloc_wksp();
    ret = glp_mpl_read_model(tran, modelFileLocation.c_str(), 0);
    if (ret != 0) {
         fprintf(stderr, "Error on translating model\n");
	 goto skip;
    }
    
    ret = glp_mpl_read_data(tran, dataFileLocation.c_str());
    if (ret != 0) {
         fprintf(stderr, "Error on translating data\n");
	 goto skip;
    }

    ret = glp_mpl_generate(tran, NULL);
    if (ret != 0) {
      fprintf(stderr, "Error on generating model\n");
      goto skip;
    }

    glp_mpl_build_prob(tran, P);

    // glp_adv_basis(P, 0);
    glp_simplex(P, NULL);
    // glp_exact(P, NULL);
    glp_intopt(P, NULL);

    int z = glp_mip_obj_val(P);
    std::cout << "Objective value z = " << z << std::endl;

    int number_of_col = glp_get_num_cols(P);
    std::cout << "number_of_col " << number_of_col << std::endl;

    for (int i = 0; i < number_of_col; i++) {
      std::string variable_name = glp_get_col_name(P, i + 1);
      int variable_result = glp_mip_col_val(P, i + 1);
      std::cout << variable_name << " " << variable_result << std::endl;
    }

  } catch (const std::exception &e) {
    std::cerr << e.what() << std::endl;
    return 1;
  } catch (const char *msg) {
    std::cout << "*****************" << std::endl;
    std::cout << "Error: " << msg << std::endl;
    std::cout << "*****************" << std::endl;
    return 1;
  }

skip: glp_mpl_free_wksp(tran);
      glp_delete_prob(P);

  return 0;
}
