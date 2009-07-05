<?php
  /** composite operation "update" */
  $running_model_11e47202c05_28 = false;
  function do_model_11e47202c05_28($model_11e47202c05_2b) {
    // operation: update
    global $running_model_11e47202c05_28;
    if ($running_model_11e47202c05_28 == false) {
      $running_model_11e47202c05_28 = true; // prevent loops
      // has this operation got a fail handler?
      // execute the operation
      // flow starts here
      // chained operation setPropertyToValue
      // outFlows first to = org.openiaml.model.model.impl.ApplicationElementPropertyImpl@1839d2e (isGenerated: true, id: model.11e47202c05.2a, name: fieldValue, defaultValue: null)
      queue_set_application_property("visual_11e37df5bce_1c", model_11e47202c05_2b);
      // do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@1f70a5d (isGenerated: true, id: operations.11e47202c63.e)
      // do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@1f70a5d (isGenerated: true, id: operations.11e47202c63.e)
      // continue chained operations
      // continue chained operations
      $running_model_11e47202c05_28 = false;
    }
  }
  /** composite operation "check key" */
  $running_model_11e47202c05_19 = false;
  function do_model_11e47202c05_19($model_11e47202c05_1a) {
    // operation: check key
    global $running_model_11e47202c05_19;
    if ($running_model_11e47202c05_19 == false) {
      $running_model_11e47202c05_19 = true; // prevent loops
      // has this operation got a fail handler?
      // fail edge org.openiaml.model.model.wires.impl.NavigateWireImpl@bce3d7 (isGenerated: true, id: wires.11e47202c63.a) (name: fail, overridden: false)
      try {
        // execute the operation
        // flow starts here
        // decision operation equal?
        if ($model_11e47202c05_1a == "42") {
          // passed (exactly one pass)
          // do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@1f33149 (isGenerated: true, id: operations.11e47202c63.9)
        } else {
          // failed (exactly one fail)
          throw new IamlRuntimeException("Error: Key check failed. You may need to login.");
        }
        // continue chained operations
        // continue chained operations
        // ignoring fail wire
      } catch (IamlRuntimeException $e) {
        log_message("Caught exception $e");
        // we need to navigate to the fail page
        $url = "visual_11e47202c63_4.php?fail=" . urlencode($e->getMessage());
        log_message("Redirecting to '$url' (fail)");
        header("Location: $url");
        die;
      }
      $running_model_11e47202c05_19 = false;
    }
  }
  /** composite operation "do logout" */
  $running_model_11e47202c05_20 = false;
  function do_model_11e47202c05_20() {
    // operation: do logout
    global $running_model_11e47202c05_20;
    if ($running_model_11e47202c05_20 == false) {
      $running_model_11e47202c05_20 = true; // prevent loops
      // has this operation got a fail handler?
      // execute the operation
      // flow starts here
      // chained operation setPropertyToValue
      // outFlows first to = org.openiaml.model.model.impl.ApplicationElementPropertyImpl@13641d6 (isGenerated: false, id: model.11e37b8d89c.17, name: my login key, defaultValue: null)
      $_SESSION["model_11e37b8d89c_17"] = "null" /* static value "model_11e47202c05_21" */;
      // do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@cedf4e (isGenerated: true, id: operations.11e47202c63.c)
      // do nothing org.openiaml.model.model.operations.impl.FinishNodeImpl@cedf4e (isGenerated: true, id: operations.11e47202c63.c)
      // continue chained operations
      // continue chained operations
      // navigate to another page
      $url = "visual_11e19f1b1ea_2.php";
      log_message("Redirecting to '$url'");
      header("Location: $url");
      die;
      $running_model_11e47202c05_20 = false;
    }
  } ?><html>
</html>